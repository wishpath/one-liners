package org.sa.service;

import org.sa.AiClient;
import org.sa.concepts.Concepts;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


public class InstructionTextForUser {
  private static final Path INSTRUCTIONS_FOR_USER_FILEPATH = Paths.get("src/main/java/org/sa/storage/instructions_for_user.csv");
  public Map<String, String[]> key_userInstruction;
  private final AiClient ai = new AiClient().setModelGpt5();

  public InstructionTextForUser(Concepts concepts) {
    this.key_userInstruction = loadAdditionalInstructions(concepts);
  }

  public String getInstructionForUserForConcept(Map.Entry<String, String> concept, String instructionToEvaluateUserInput, AdditionalInstructionsToEvaluate instructionsToEvaluate) {
    if (!instructionsToEvaluate.key_instructions.containsKey(concept.getKey()))
      return "Please explain this: " + Colors.RED + concept.getKey() + Colors.RESET + "\n";

    if (key_userInstruction.containsKey(concept.getKey()))
      return key_userInstruction.get(concept.getKey())[0];

    SimpleColorPrint.red("AI is generating instruction for the user...");
    String instructionForUserForConcept = ai.getAnswer(
        "Write one simple instruction (max 200 characters) for a learner." +
            "\nThey must describe the concept " + Colors.RED + concept.getKey() + Colors.RESET + " to score 10/10." +
            "\nDo not include, restate, or hint at any information from the definition or evaluation rules." +
            "\nDo not explain what the concept means or does." +
            "\nTell the learner which key points their answer must mention, but keep the wording easy to read." +
            "\nThe instruction must be ONE single line with no line breaks." +
            "\nSeparate content elements using semicolons." +
            "\nUse the concept key exactly as shown, including ANSI codes." +
            "\nYour output must contain only that single-line instruction text and the concept key in red." +
            "\n\nDefinition (context only): " + concept.getValue() +
            "\nEvaluation rules (context only): " + instructionToEvaluateUserInput
    );



    instructionForUserForConcept = instructionForUserForConcept.replaceAll(",", ";");
    appendUserInstructionToFile(concept.getKey(), instructionForUserForConcept);

    return instructionForUserForConcept;
  }

  private Map<String, String[]> loadAdditionalInstructions(Concepts concepts) {
    Map<String, String[]> key_userInstruction = new HashMap<>();

    try (Stream<String> lines = Files.lines(INSTRUCTIONS_FOR_USER_FILEPATH)) {
      lines.forEach(line -> {
        String[] key_instruction = line.split(",", 2);
        if (key_instruction.length == 1) throw new RuntimeException("LINE DOES NOT CONTAIN COMMA");
        String key = key_instruction[0];
        if (!concepts.keyDefinition.containsKey(key)) throw new RuntimeException("KEY DOES NOT EXIST IN keyDefinition: " + key);
        if (key_userInstruction.containsKey(key)) throw new RuntimeException("KEY SHOULD BE UNIQUE: " + key);
        String[] instructions = key_instruction[1].split(",");
        key_userInstruction.put(key, instructions);
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return key_userInstruction;
  }

  private void appendUserInstructionToFile(String key, String instruction) {
    try {
      String line = key + "," + instruction + System.lineSeparator();
      Files.write(INSTRUCTIONS_FOR_USER_FILEPATH, line.getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
