package org.sa.actions;

import org.sa.concepts.Concepts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class IndividualInstructionFromFile {
  private static final Path ADDITIONAL_INSTRUCTIONS_FILEPATH = Paths.get("src/main/java/org/sa/storage/instructions.csv");
  Map<String, String[]> key_instructions;
  Concepts concepts;

  public IndividualInstructionFromFile(Concepts concepts) {
    this.concepts = concepts;
    this.key_instructions = loadAdditionalInstructions();
  }

  private Map<String, String[]> loadAdditionalInstructions() {
    Map<String, String[]> key_instructions = new HashMap<>();

    try (Stream<String> lines = Files.lines(ADDITIONAL_INSTRUCTIONS_FILEPATH)) {
      lines.forEach(line -> {
        String[] key_instruction = line.split(",", 2);
        if (key_instruction.length == 1) throw new RuntimeException("LINE DOES NOT CONTAIN COMMA");
        String key = key_instruction[0];
        if (!concepts.keyDefinition.containsKey(key)) throw new RuntimeException("KEY DOES NOT EXIST IN keyDefinition: " + key);
        if (key_instructions.containsKey(key)) throw new RuntimeException("KEY SHOULD BE UNIQUE: " + key);
        String[] instructions = key_instruction[1].split(",");
        key_instructions.put(key, instructions);
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return key_instructions;
  }

  public String getIndividualInstructions(String key) {
    StringBuilder sb = new StringBuilder();
    for (String instruction : key_instructions.get(key))
      sb.append(instruction + "\n");
    return sb.toString();
  }
}
