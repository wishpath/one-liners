package org.sa.dto;

import org.sa.a_config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.util.StringConsoleUtil;

public class ConceptDTO {
  public String key;
  public String definition;
  public String userAnswerInstruction = Props.DEFAULT_USER_ANSWER_INSTRUCTION;
  public String aiEvaluateInstruction = Props.DEFAULT_AI_EVALUATION_INSTRUCTION;

  public String topic;
  public Integer score;

  public ConceptDTO(String key, String definition) {
    this.key = key;
    this.definition = definition;
    this.score = 0;
  }

  public ConceptDTO(String key, String definition, String userAnswerInstruction, String aiEvaluationInstruction, String topic) {
    this.key = key;
    this.definition = definition;
    this.userAnswerInstruction = userAnswerInstruction;
    this.aiEvaluateInstruction = aiEvaluationInstruction;
    this.topic = topic;
    this.score = 0;
  }

  public void printUserInstruction() {
    if (userAnswerInstruction.equals(Props.DEFAULT_USER_ANSWER_INSTRUCTION)) {
      SimpleColorPrint.blueInLine("\n" + userAnswerInstruction);
      SimpleColorPrint.red(key);
    }
    else {
      String formatted = "\n\n" + userAnswerInstruction
          .replace("****", "\n\t\t\t\t")
          .replace("***", "\n\t\t\t")
          .replace("**", "\n\t\t")
          .replace("*", "\n\t") + "\n";
      StringConsoleUtil.printStringWithFragmentHighlighted(key, formatted, Colors.BLUE, Colors.RED);
    }
  }

  public void printScore() {
    SimpleColorPrint.blueInLine("The score of concept '");
    SimpleColorPrint.redInLine(key);
    SimpleColorPrint.blueInLine("' is: ");
    SimpleColorPrint.red(score + "\n");
  }
}
