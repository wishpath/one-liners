package org.sa.service;

import org.sa.a_config.FilePath;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    //load keys and definitions from file
    try {
      for (Path subtopicPath : Files.walk(FilePath.TOPICS_PUBLIC).filter(p -> p.toString().endsWith(".concepts")).toList())
        Files.lines(subtopicPath)
            .filter(line -> line.contains("="))
            .forEach(line -> {
              String[] arr = line.split("=", 2);
              if (arr[0].contains(";")) throw new RuntimeException("Key '" + arr[0] + "' should not contain semicolons (;)");
              if (arr[0].contains(",")) throw new RuntimeException("Key '" + arr[0] + "' should not contain commas (,)");

              ConceptDTO repeated = key_concept.put(arr[0], new ConceptDTO(arr[0], arr[1]));
              if (repeated != null) {
                SimpleColorPrint.redInLine("The repeated key: ");
                SimpleColorPrint.blueInLine(arr[0]);
                SimpleColorPrint.redInLine(", the definitions: ");
                SimpleColorPrint.blueInLine(arr[1]);
                SimpleColorPrint.redInLine(" and ");
                SimpleColorPrint.blue(repeated.definition);
              }
            });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // load and assign evaluateInstructions
    try (Stream<String> lines = Files.lines(FilePath.INSTRUCTIONS_TO_EVALUATE)) {
      lines.forEach(line -> {
        String[] key_instruction = line.split(",", 2);
        String key = key_instruction[0];
        String instructions = key_instruction[1];
        ConceptDTO conceptDTO = key_concept.get(key);
        if (key_instruction.length != 2) throw new RuntimeException("EVALUATE INSTRUCTION LINE DOES NOT CONTAIN EXACTLY ONE COMMA");
        if (conceptDTO == null) throw new RuntimeException("EVALUATE INSTRUCTION KEY DOES NOT EXIST IN key_concept: " + key);
        if (conceptDTO.aiEvaluateInstruction != null) throw new RuntimeException("EVALUATE INSTRUCTION KEY SHOULD BE UNIQUE AND NOT REPEATED: " + key);
        conceptDTO.aiEvaluateInstruction = instructions;
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //load scores to type "Properties" from file
    Properties scoreProperties = new Properties();
    try (Reader reader = Files.newBufferedReader(FilePath.SCORE_PATH, StandardCharsets.UTF_8)) {
      scoreProperties.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //assign scores from type "Properties"
    for (Map.Entry<Object, Object> entryOfKey_scoreProperties : scoreProperties.entrySet()) {
      String propertiesKey = (String) entryOfKey_scoreProperties.getKey();
      int propertiesScore = Integer.parseInt((String) entryOfKey_scoreProperties.getValue());
      if (!key_concept.containsKey(propertiesKey)) {
        System.err.println("Detected deleted key, that has score. Deleted key: " + Colors.RED + propertiesKey + Colors.RESET);
        continue;
      }
      key_concept.get(propertiesKey).score = propertiesScore;
    }

    TEMP_check_if_concepts_in_separate_files_match(key_concept);
    return key_concept;
  }

  private static void TEMP_check_if_concepts_in_separate_files_match(Map<String, ConceptDTO> key_concept) {
    key_concept.forEach((key, conceptDTO) -> {
      //key can be in any topic, topics are directories in FilePath.CONCEPT_FILES_PUBLIC
      //check if key exists in any topic
      AtomicInteger keyExistsTimes = new AtomicInteger();
      try (Stream<Path> topics = Files.list(FilePath.CONCEPT_FILES_PUBLIC)) {
        topics.forEach(topicPath -> {
          String topicName = topicPath.getFileName().toString();
          if (Files.isRegularFile(topicPath.resolve(key))) keyExistsTimes.getAndIncrement();
        });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      if (keyExistsTimes.get() != 1) throw new IllegalStateException("KEY NAMED FILE SHOULD EXIST EXACTLY ONCE");
    });
    System.out.println("GREAT, ALL THE CONCEPTS HAS A CORRECT SEPARATE FILE");
  }
}
