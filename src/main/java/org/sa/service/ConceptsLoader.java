package org.sa.service;

import org.sa.a_config.FilePath;
import org.sa.a_config.Props;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

//loads key, definition, score, evaluateInstruction
public class ConceptsLoader {


  public Map<String, ConceptDTO> key_concept = loadConceptsWithAttributes2();
//  public Map<String, ConceptDTO> key_concept = loadConceptsWithAttributes();
//  public boolean match = TEMP_validate_old_and_new_math_matches(key_concept2, key_concept);

//  private boolean TEMP_validate_old_and_new_math_matches(Map<String, ConceptDTO> keyConcept2, Map<String, ConceptDTO> keyConcept) {
//    Map<String, ConceptDTO> key_concept = ConceptsLoader.loadConceptsWithAttributes();
//    Map<String, ConceptDTO> key_concept2 = ConceptsLoader.loadConceptsWithAttributes2();
//    key_concept2.forEach((key, concept) -> System.out.println(key + " " + concept.score));
//
//// check size first
//    if (key_concept.size() != key_concept2.size()) throw new IllegalStateException("Size mismatch");
//
//// check all keys exist
//    if (!key_concept.keySet().equals(key_concept2.keySet())) throw new IllegalStateException("Keys mismatch");
//
//// check each ConceptDTO fields
//    key_concept.forEach((key, oldConcept) -> {
//      ConceptDTO newConcept = key_concept2.get(key);
//      if (!oldConcept.definition.equals(newConcept.definition))
//        throw new IllegalStateException(
//            "Definition mismatch for key: " + key + "\n" +
//                "old: " + oldConcept.definition + "\n" +
//                "new: " + newConcept.definition
//        );
//
//      if (!oldConcept.userAnswerInstruction.equals(newConcept.userAnswerInstruction))
//        throw new IllegalStateException(
//            "User answer instruction mismatch for key: " + key + "\n" +
//                "old: " + oldConcept.userAnswerInstruction + "\n" +
//                "new: " + newConcept.userAnswerInstruction
//        );
//
//      if (!oldConcept.aiEvaluateInstruction.trim().equals(newConcept.aiEvaluateInstruction))
//        throw new IllegalStateException(
//            "AI evaluate instruction mismatch for key: " + key + "\n" +
//                "old: " + oldConcept.aiEvaluateInstruction + "\n" +
//                "new: " + newConcept.aiEvaluateInstruction
//        );
//
//      if (oldConcept.score != newConcept.score)
//        throw new IllegalStateException(
//            "Score mismatch for key: " + key + "\n" +
//                "old: " + oldConcept.score + "\n" +
//                "new: " + newConcept.score
//        );
//    });
//
//    System.out.println("All concepts match perfectly!");
//    return true;
//  }

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
        if (!conceptDTO.aiEvaluateInstruction.equals(Props.DEFAULT_AI_EVALUATION_INSTRUCTION)) throw new RuntimeException("EVALUATE INSTRUCTION KEY SHOULD BE UNIQUE AND NOT REPEATED: " + key);
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
    AtomicInteger defaultAiInstructionCount = new AtomicInteger();
    AtomicInteger specificAiInstructionCount = new AtomicInteger();
    key_concept.forEach((key, conceptDTO) -> {
      //key can be in any topic, topics are directories in FilePath.CONCEPT_FILES_PUBLIC
      //check if key exists in any topic
      AtomicInteger keyExistsTimes = new AtomicInteger();


      try (Stream<Path> topics = Files.list(FilePath.CONCEPT_FILES_PUBLIC)) {
        topics.forEach(topicPath -> {
          String topicNameAsDirectory = topicPath.getFileName().toString();
          Path filePath = topicPath.resolve(key);
          if (Files.isRegularFile(filePath)) {
            keyExistsTimes.getAndIncrement();
            List<String> fileLines = FileUtil.listLines(filePath.toFile());

            //check key
            String contentKey = fileLines.get(0);
            if (!key.equals(contentKey)) throw new IllegalArgumentException("KEY MISMATCH");
            if (!conceptDTO.key.equals(contentKey)) throw new IllegalArgumentException("KEY MISMATCH");
            //System.out.println(key);

            //check definition
            String contentDefinition = fileLines.get(1);
            if (!conceptDTO.definition.equals(contentDefinition)) throw new IllegalArgumentException("DEFINITION MISMATCH");

            //check user instruction
            String contentUserAnswerInstruction = fileLines.get(2);
            if (!conceptDTO.userAnswerInstruction.equals(contentUserAnswerInstruction)) throw new IllegalArgumentException("USER INSTRUCTION MISMATCH");

            //check ai instruction
            String contentAiEvaluationInstruction = fileLines.get(3);
            if (!contentAiEvaluationInstruction.trim().equals(conceptDTO.aiEvaluateInstruction.trim()))
                throw new IllegalArgumentException("AI INSTRUCTION MISMATCH: \n" + contentAiEvaluationInstruction.trim() + " \n" + conceptDTO.aiEvaluateInstruction.trim());
            if (contentAiEvaluationInstruction.equals(Props.DEFAULT_AI_EVALUATION_INSTRUCTION)) defaultAiInstructionCount.getAndIncrement();
            else specificAiInstructionCount.getAndIncrement();

            //check topic
            String contentTopic = fileLines.get(4);
            if (!contentTopic.equals(topicNameAsDirectory)) throw new IllegalArgumentException("TOPIC MISMATCH: " + topicNameAsDirectory);
          }
        });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      if (keyExistsTimes.get() != 1) throw new IllegalStateException("KEY NAMED FILE SHOULD EXIST EXACTLY ONCE");

    });
    System.out.println("GREAT, ALL THE CONCEPTS HAS A CORRECT SEPARATE FILE");
    System.out.println("DEFAULT INSTRUCTION COUNT: " + defaultAiInstructionCount.get());
    System.out.println("SPECIFIC INSTRUCTION COUNT: " + specificAiInstructionCount.get());


  }

  public static Map<String, ConceptDTO> loadConceptsWithAttributes2(){
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
