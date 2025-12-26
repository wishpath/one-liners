package org.sa.service;

import org.sa.AiClient;
import org.sa.a_config.FilePath;
import org.sa.a_config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.util.StringConsoleUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class AiService {
  private final ConceptPicker act;
  private final AiClient evaluatorAi = new AiClient().setModelGpt4oMini();
  private AiClient answersAi = new AiClient().setModelGpt4oMini();
  private final ScoreService scoreService;
  private final NotTodayService notTodayService;

  public AiService(ConceptPicker act, ScoreService scoreService, NotTodayService notTodayService) {
    this.act = act;
    this.scoreService = scoreService;
    this.notTodayService = notTodayService;
  }

  public ConceptDTO evaluateUserExplanationWithAI(ConceptDTO concept, String userInputDefinitionAttempt) throws IOException {

    String instructionToEvaluateUserInput = getInstructionToEvaluateUserInput(concept, userInputDefinitionAttempt);
    System.out.println("TEMP PRINTING INSTRUCTION HERE");
    System.out.println(instructionToEvaluateUserInput);
    System.out.println(concept.aiEvaluateInstruction);

    //AI evaluation
    String answer = evaluatorAi.getAnswer(instructionToEvaluateUserInput) + "\n";

    //get evaluation out of 10
    String evaluationOutOfTenString = extractEvaluationString(answer);
    int evaluationOutOfTen = Integer.parseInt(evaluationOutOfTenString.split("/")[0]);
    if ("".equals(evaluationOutOfTenString)) {
      SimpleColorPrint.red("The AI has not provided the evaluation. Try again. AI answer: \n" + answer);
      return concept;
    }

    StringConsoleUtil.printStringWithFragmentHighlighted(evaluationOutOfTenString, answer, Colors.YELLOW, Colors.RED);

    //memorize answer
    String userAttemptedDefinition = userInputDefinitionAttempt.replace(",", ";");
    String recordLine = String.join(",", concept.key, userAttemptedDefinition, String.valueOf(evaluationOutOfTen), LocalDateTime.now().toString()) + "\n";
    try (BufferedWriter writer = Files.newBufferedWriter(FilePath.ATTEMPTED_ANSWERS, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      writer.write(recordLine);
    }

    //score, put to "not_today", print default answer
    scoreService.incrementScore(concept, evaluationOutOfTen < 7 ? -1 : evaluationOutOfTen <= 8 ? 1 : evaluationOutOfTen == 9 ? 2 : 4);
    notTodayService.dontLearnThisToday(concept);
    SimpleColorPrint.blueInLine("The default definition: ");
    SimpleColorPrint.normal(concept.definition + "\n");

    return act.pickConceptWithLowestScore();
  }

  private String extractEvaluationString(String s) {
    return Pattern.compile("\\b([0-9]|10)/10\\b").matcher(s).results().map(matchResult -> matchResult.group()).findFirst().orElse("");
  }

  public void askAi(String input) {
    SimpleColorPrint.yellow(answersAi.getAnswer(input) + "\n");
  }

  public static String getInstructionToEvaluateUserInput(ConceptDTO concept, String input) {
    return concept.aiEvaluateInstruction.equals(Props.DEFAULT_AI_EVALUATION_INSTRUCTION)
        ? getDefaultInstructionToEvaluateUserInput(concept, input)
        : getIndividualInstructionToEvaluateUserInput(concept, input);
  }

  public static String getDefaultInstructionToEvaluateUserInput(ConceptDTO concept, String input) {
    String instruction =
        "Is this a good key and definition: key: \"" + concept.key + "\", and definition: \"" + input + "\". " +
            "\n A. Step 1 - Evaluate the answer by asking: 'Does this capture the essence?' (aim to be positive)." +
            "\n B. If some details are missing (but they are not mentioned in the 'additional instructions') you can still cap the evaluation at 10/10." +
            "\n C. If the definition is a lot like this one, rate 10/10: " + concept.definition + ", but other definitions might get a perfect rating as well." +
            "\n D. If the essence is ALMOST there, rate 9/10." +
            "\n E. If the essence is somewhat touched, rate 8/10." +
            "\n F. Think of an answer in up to 10 words - if you can't come up with a better one, rate 10/10." +
            "\n G. If the answer is completely off, rate 0/10." +
            "\n H. If the answer is somewhat acceptable, rate 7/10." +

            //acronyms
            "\n I. If the key is an acronym, definitions should include the exact matching word for each letter in the definition (e.g., 'Intelligence Quotient' for IQ); case does not matter." +
            "\n J. If the key is an acronym, each core expanded word must be spelled exactly (−1 point per misspelling); Ignore if letters are lowercase or uppercase\n" +
            "\n K. If the key is an acronym, each core expanded word — even if misspelled — must clearly match the key’s intended word; wrong words aren’t accepted (e.g., for “SSL”: “securing” OK (only gramatical form is different), “sekure” −1 point (misspell), “service” rejected (totally wrong word)).\n" +

            "\n Step 2 - If the evaluation is less than 7/10, provide the correct answer (if 7/10 to 10/10 — do not provide the answer)." +
            "\n Your entire response should be up to 300 characters. \n";
    return instruction;
  }

  public static String getIndividualInstructionToEvaluateUserInput(ConceptDTO concept, String input) {
    String instruction =
            "User has attempted to describe this key: "
            + "\n\"" + concept.key + "\"\n\n"
            + "User has provided this answer: "
            + "\n\"" + input + "\"\n\n"
            + "Your job is to evaluate user's answer from 0/10 to 10/10. This is how you should do it: "
            + "\n\"" + concept.aiEvaluateInstruction + "\"\n\n"
            + "Don't be too strict when evaluating.\n\n"
            + "Just for the context, the default example of definitions in the system is: "
            + "\n\"" + concept.definition + "\"\n\n\n"
            + "This is what you should output: "
            + "\nFirst line should only be evaluation in this format, nothing extra: \n"
            + "*/10"
            + "\nThen next line: reason your evaluation decision in less than 200 characters (show the MATH of provided points)\n"
            + "Then optional part in case the evaluation was less than 7/10:"
            + "\nIn the next line: Formulate what would be a better answer in 10 words or less";
    return instruction;
  }
}
