package org.sa.service;

import org.sa.a_config.FilePath;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

//loads key, definition, score, evaluateInstruction
public class ConceptsLoader {


  public Map<String, ConceptDTO> key_concept = loadConceptsWithAttributes();

  public final TreeMap<Integer, Set<String>> score_keySet = mapScores(key_concept);

  public static TreeMap<Integer, Set<String>> mapScores(Map<String, ConceptDTO> key_concept) {
    TreeMap<Integer, Set<String>> score_keyList = new TreeMap<>(); //auto ascending map
    for (ConceptDTO c : key_concept.values()) score_keyList.computeIfAbsent(c.score, k -> new HashSet<>()).add(c.key);
    return score_keyList;
  }

  public static Map<String, ConceptDTO> loadConceptsWithAttributes(){
    Map<String, ConceptDTO> key_concept = new HashMap<>();
    Stream<Path> topics = FileUtil.listDirectories(FilePath.CONCEPT_FILES_PUBLIC);
    topics.forEach(topicPath -> {
      String topicNameAsDirectory = topicPath.getFileName().toString();
      for (File conceptFile : FileUtil.listFiles(topicPath)) {
        if (!conceptFile.isFile()) throw new IllegalArgumentException("IN THE TOPIC FOLDER THERE SHOULD ONLY BE CONCEPT FILES");
        String keyAsFilename = conceptFile.getName().trim();
        List<String> fileLines = FileUtil.listLines(conceptFile);
        //key
        String contentKey = fileLines.get(0).trim();
        if (!keyAsFilename.equals(contentKey)) throw new IllegalArgumentException("KEY MISMATCH " + contentKey + " " + keyAsFilename);
        if (contentKey.contains(";")) throw new RuntimeException("Key '" + contentKey + "' should not contain semicolons (;)");
        if (contentKey.contains(",")) throw new RuntimeException("Key '" + contentKey + "' should not contain commas (,)");
        if (contentKey.matches(".*[\\\\/:*?\"<>|].*")) throw new IllegalArgumentException("key should be filename-friendly: " + contentKey);
        if (contentKey.endsWith(".")) throw new IllegalArgumentException("key cannot end with a dot: " + contentKey);
        //definition
        String contentDefinition = fileLines.get(1);
        //user instruction
        String contentUserAnswerInstruction = fileLines.get(2);
        //ai instruction
        String contentAiEvaluationInstruction = fileLines.get(3);
        //topic
        String contentTopic = fileLines.get(4);
        if (!contentTopic.equals(topicNameAsDirectory)) throw new IllegalArgumentException("TOPIC MISMATCH: " + topicNameAsDirectory);
        ConceptDTO repeated = key_concept.put(
            contentKey,
            new ConceptDTO(contentKey, contentDefinition, contentUserAnswerInstruction, contentAiEvaluationInstruction, contentTopic)
        );
        if (repeated != null) {
          SimpleColorPrint.redInLine("The repeated key: ");
          SimpleColorPrint.blueInLine(contentKey);
          SimpleColorPrint.redInLine(", the definitions: ");
          SimpleColorPrint.blueInLine(contentDefinition);
          SimpleColorPrint.redInLine(" and ");
          SimpleColorPrint.blue(repeated.definition);
        }
      }
    });
    //load scores to type "Properties" from file  ALTERNATIVE
    Properties scoreProperties = new Properties();
    try (Reader reader = Files.newBufferedReader(FilePath.SCORE_PATH, StandardCharsets.UTF_8)) {
      scoreProperties.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //assign scores from type "Properties"  ALTERNATIVE
    for (Map.Entry<Object, Object> entryOfKey_scoreProperties : scoreProperties.entrySet()) {
      String propertiesKey = (String) entryOfKey_scoreProperties.getKey();
      int propertiesScore = Integer.parseInt((String) entryOfKey_scoreProperties.getValue());
      if (!key_concept.containsKey(propertiesKey)) {
        System.err.println("Detected deleted key, that has score. Deleted key: " + Colors.RED + propertiesKey + Colors.RESET);
        continue;
      }
      key_concept.get(propertiesKey).score = propertiesScore;
    }

    return key_concept;
  }
}
