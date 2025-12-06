package org.sa.service;

import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public class A_ConceptsLoader {
  public static final Path TOPICS_PUBLIC = Paths.get("src/main/java/org/sa/storage/concepts/topics");

  public static Map<String, ConceptDTO> loadConceptsCheckRepeated(){
    Map<String, ConceptDTO> key_concept = new HashMap<>();

    //load keys and definitions from file
    try {
      for (Path subtopicPath : Files.walk(TOPICS_PUBLIC).filter(p -> p.toString().endsWith(".concepts")).toList())
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

    // load and assign evaluateInstructions from file
    try (Stream<String> lines = Files.lines(Paths.get("src/main/java/org/sa/storage/instructions_to_evaluate.csv"))) {
      lines.forEach(line -> {
        String[] key_instruction = line.split(",", 2);
        String key = key_instruction[0];
        String instructions = key_instruction[1];
        ConceptDTO conceptDTO = key_concept.get(key);
        if (key_instruction.length != 2) throw new RuntimeException("EVALUATE INSTRUCTION LINE DOES NOT CONTAIN EXACTLY ONE COMMA");
        if (conceptDTO == null) throw new RuntimeException("EVALUATE INSTRUCTION KEY DOES NOT EXIST IN key_concept: " + key);
        if (conceptDTO.evaluateInstruction != null) throw new RuntimeException("EVALUATE INSTRUCTION KEY SHOULD BE UNIQUE AND NOT REPEATED: " + key);
        conceptDTO.evaluateInstruction = instructions;
        System.out.println("A_ConceptsLoader: " + conceptDTO.evaluateInstruction);
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //load scores to type "Properties" from file
    Properties scoreProperties = new Properties();
    try (Reader reader = Files.newBufferedReader(org.sa.a_config.Paths.SCORE_PATH, StandardCharsets.UTF_8)) {
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

    return key_concept;
  }
}
