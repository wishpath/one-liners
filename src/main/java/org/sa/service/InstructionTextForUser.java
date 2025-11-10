package org.sa.service;

import org.sa.AiClient;
import org.sa.console.Colors;

import java.util.Map;

public class InstructionTextForUser {
  private final AiClient ai;

  public InstructionTextForUser(AiClient ai) {
    this.ai = ai;
  }

  public String getInstructionForUserForConcept(Map.Entry<String, String> concept, String instructionToEvaluateUserInput) {
//    String instructionForUserForConcept = ai.getAnswer(
//        "You are writing a short instruction (max 200 characters) for a learner in a concept-learning app." +
//        "\nThe learner must describe the concept \033[31m" + concept.getKey() + "\033[0m to earn 10/10." +
//        "\nYou will see the definition and evaluation rules so you understand what qualifies as a perfect answer." +
//        "\nDO NOT include, restate, paraphrase, or hint at any information, terms, or examples from those texts." +
//        "\nDO NOT describe what the concept means or does." +
//        "\nOnly tell the learner *how* to answer (for example: 'include all words of an acronym', 'describe its purpose', 'explain relationships')." +
//        "\nYour output must mention the concept key in red and nothing else besides the instruction itself." +
//        "\n\nDefinition (for context only): " + concept.getValue() +
//        "\nEvaluation instructions (for context only): " + instructionToEvaluateUserInput);
    return "Please explain this: " + Colors.RED + concept.getKey() + Colors.RESET + "\n";

  }
}
