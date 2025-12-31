package org.sa.service;

import org.sa.a_config.FilePath;
import org.sa.a_config.Props;
import org.sa.console.Colors;
import org.sa.dto.ConceptDTO;
import org.sa.util.FileUtil;

import java.io.File;
import java.util.*;

//loads key, definition, score, evaluateInstruction
public class ConceptsLoader {

  public static final int KEY = 0;
  public static final int DEFINITION = 1;
  public static final int USER_ANSWER_INSTRUCTION = 2;
  public static final int AI_EVALUATION_INSTRUCTION = 3;
  public static final int TOPIC = 4;
  public Map<String, ConceptDTO> key_concept;

  public final TreeMap<Integer, Set<String>> score_keySet;

  public ConceptsLoader() {
    this.key_concept = loadConceptsWithAllAttributesExceptScore();
    assignScores();
    this.score_keySet = mapScores(key_concept);
  }

  private void assignScores() {
    for (Map.Entry<Object, Object> entryOfKey_scoreProperties : FileUtil.loadPropertiesFileAsMapEntrySet(FilePath.SCORE_PATH)) {
      String propertiesKey = (String) entryOfKey_scoreProperties.getKey();
      int propertiesScore = Integer.parseInt((String) entryOfKey_scoreProperties.getValue());
      if (!key_concept.containsKey(propertiesKey)) {
        System.err.println("Detected deleted key, that has score. Deleted key: " + Colors.RED + propertiesKey + Colors.RESET);
        continue;
      }
      key_concept.get(propertiesKey).score = propertiesScore;
    }
  }

  public static TreeMap<Integer, Set<String>> mapScores(Map<String, ConceptDTO> key_concept) {
    TreeMap<Integer, Set<String>> score_keyList = new TreeMap<>(); //auto ascending map
    for (ConceptDTO c : key_concept.values()) score_keyList.computeIfAbsent(c.score, k -> new HashSet<>()).add(c.key);
    return score_keyList;
  }

  public static Map<String, ConceptDTO> loadConceptsWithAllAttributesExceptScore() {
    Map<String, ConceptDTO> key_concept = new HashMap<>();

    FileUtil.listDirectories(FilePath.CONCEPT_FILES_PUBLIC).forEach(topicPath -> {
      for (File conceptFile : FileUtil.listFiles(topicPath)) {
        if (!conceptFile.isFile()) throw new IllegalArgumentException("IN THE TOPIC FOLDER THERE SHOULD ONLY BE CONCEPT FILES");
        List<String> fileLines = FileUtil.listLines(conceptFile);
        String contentKey = getValidContentKey(fileLines.get(KEY).trim(), conceptFile.getName().trim(), key_concept);
        String contentUserAnswerInstruction = getValidatedUserAnswerInstruction(fileLines.get(USER_ANSWER_INSTRUCTION), contentKey);
        key_concept.put(
            contentKey,
            new ConceptDTO(
                contentKey,
                fileLines.get(DEFINITION),
                contentUserAnswerInstruction,
                fileLines.get(AI_EVALUATION_INSTRUCTION),
                getValidatedContentTopic(fileLines.get(TOPIC), topicPath.getFileName().toString()))
        );
      }
    });
    return key_concept;
  }

  private static String getValidatedUserAnswerInstruction(String UserAnswerInstructionLineInFile, String contentKey) {
    if (UserAnswerInstructionLineInFile.equals(Props.DEFAULT_USER_ANSWER_INSTRUCTION))
      return Props.DEFAULT_USER_ANSWER_INSTRUCTION;

    if (!UserAnswerInstructionLineInFile.contains(contentKey))
      throw new IllegalArgumentException(
          "User answer instruction must contain EXACT 'key'\n\tUser answer instruction:\n\t\t" + UserAnswerInstructionLineInFile + "\n\tKey: " + contentKey);

    return UserAnswerInstructionLineInFile;
  }

  private static String getValidatedContentTopic(String contentTopic, String topicNameAsDirectory) {
    if (!contentTopic.equals(topicNameAsDirectory)) throw new IllegalArgumentException("TOPIC MISMATCH: " + topicNameAsDirectory);
    return contentTopic;
  }

  private static String getValidContentKey(String contentKey, String keyAsFilename, Map<String, ConceptDTO> key_concept) {
    if (!keyAsFilename.equals(contentKey)) throw new IllegalArgumentException("KEY MISMATCH " + contentKey + " " + keyAsFilename);
    if (contentKey.contains(";")) throw new RuntimeException("Key '" + contentKey + "' should not contain semicolons (;)");
    if (contentKey.contains(",")) throw new RuntimeException("Key '" + contentKey + "' should not contain commas (,)");
    if (contentKey.matches(".*[\\\\/:*?\"<>|].*")) throw new IllegalArgumentException("key should be filename-friendly: " + contentKey);
    if (contentKey.endsWith(".")) throw new IllegalArgumentException("key cannot end with a dot: " + contentKey);
    if (key_concept.containsKey(contentKey)) throw new IllegalArgumentException("key cannot repeat: " + contentKey);
    return contentKey;
  }
}
