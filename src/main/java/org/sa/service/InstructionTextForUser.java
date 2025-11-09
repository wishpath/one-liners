package org.sa.service;

import org.sa.AiClient;

import java.util.Map;

public class InstructionTextForUser {
  private final AiClient ai;

  public InstructionTextForUser(AiClient ai) {
    this.ai = ai;
  }

//  public String getInstructionForUserForConcept(Map.Entry<String, String> concept, String instructionToEvaluateUserInput) {
//    String question = "Please create an instruction for the user in the concept-learning app, " +
//        "so he would know the required info to give to get an evaluation of 10/10 for this particular concept. " +
//        "The following is the default concept description in the app:" +
//        "\"" + concept.getKey() + "=" + concept.getValue() + "\"" +
//        "User will have to follow your instruction and describe the concept. " +
//        "His answer will be evaluated by AI, by this prompt: " + instructionToEvaluateUserInput + "\n" +
//        "Please as clear and instructive for the user, but don't give out the answer and do this in less than 200 characters. " +
//        "Your instruction will be printed in the console for the user in exact form as you give now, " +
//        "so please only formulate an instruction, no comments." +
//        "For the console colors you can use ANSI escape colors, concept key has to be in red";
//    System.out.println("\n");
//    System.out.println(question);
//    System.out.println("\n\n");
//
//    return ai.getAnswer(question);
//  }
//  public String getInstructionForUserForConcept(Map.Entry<String, String> concept, String instructionToEvaluateUserInput) {
//    String question =
//        "You are creating a learning instruction for a student in a concept-learning app." +
//            "\nThe goal: tell the student what kind of information to provide so their explanation could score 10/10 for this concept." +
//            "\nYou are given full evaluation criteria and the correct definition below so you understand what is expected, " +
//            "but you must NOT reveal or hint at the actual answer, examples, or keywords from the definition." +
//            "\nFocus on describing the type of information, scope, and level of detail expected from the student." +
//            "\nKeep your output under 200 characters." +
//            "\nFor console colors, wrap the concept key in red using ANSI escape codes: \\033[31m" + concept.getKey() + "\\033[0m." +
//            "\n\nConcept definition (for your understanding only): " + concept.getValue() +
//            "\n\nEvaluation instructions (for your understanding only): " + instructionToEvaluateUserInput +
//            "\n\nNow output ONLY a short, clear instruction for the student, with no comments or explanations.";
//
//    return ai.getAnswer(question);
//  }

//  public String getInstructionForUserForConcept(Map.Entry<String, String> concept, String instructionToEvaluateUserInput) {
//    String question =
//        "You are creating a learning instruction for a student in a concept-learning app." +
//            "\nThe goal: tell the student what kind of information to provide so their explanation could score 10/10 for this concept." +
//            "\nYou are given full evaluation criteria and the correct definition below so you understand what is expected, " +
//            "but you must NOT reveal or hint at the actual answer, examples, or keywords from the definition." +
//            "\nFocus on describing the type of information, scope, and level of detail expected from the student." +
//            "\nKeep your output under 200 characters." +
//            "\nFor console colors, wrap the concept key in red using ANSI escape codes: \033[31m" + concept.getKey() + "\033[0m." +
//            "\n\nConcept definition (for your understanding only): " + concept.getValue() +
//            "\n\nEvaluation instructions (for your understanding only): " + instructionToEvaluateUserInput +
//            "\n\nNow output ONLY a short, clear instruction for the student, with no comments or explanations.";
//
//    return ai.getAnswer(question);
//  }

  public String getInstructionForUserForConcept(Map.Entry<String, String> concept, String instructionToEvaluateUserInput) {
    String question =
        "You are generating a single instructional sentence for a learner in a concept-learning app." +
            "\nThe learner must describe the concept \033[31m" + concept.getKey() + "\033[0m to earn 10/10." +
            "\nBelow are the correct definition and evaluation instructions so you understand what kind of response would qualify, " +
            "but you must NOT reveal, restate, hint at, or include any part of that definition, its examples, or its specific wording." +
            "\nYou may only tell the user *how* to think about answering (for example: 'describe its structure', 'explain its purpose', 'outline its key role'), " +
            "without giving away what it actually is." +
            "\nKeep it under 200 characters. Output only the instruction—no explanations, no examples." +
            "\n\n--- Concept definition (for your understanding only): " + concept.getValue() +
            "\n--- Evaluation instructions (for your understanding only): " + instructionToEvaluateUserInput +
            "\n\nNow write only the instruction for the learner, using the red-colored concept name where appropriate.";

    return ai.getAnswer(question);
  }

}
