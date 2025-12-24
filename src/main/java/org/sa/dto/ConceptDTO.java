package org.sa.dto;

import org.sa.a_config.Props;
import org.sa.console.SimpleColorPrint;

public class ConceptDTO {
  public String key;
  public String definition;
  public String userAnswerInstruction = "Please explain this concept: ";
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
  }

  public void printUserInstruction() {
    SimpleColorPrint.blueInLine("\n" + userAnswerInstruction);
    SimpleColorPrint.red(key);
  }

  public void printScore() {
    SimpleColorPrint.blueInLine("The score of concept '");
    SimpleColorPrint.redInLine(key);
    SimpleColorPrint.blueInLine("' is: ");
    SimpleColorPrint.red(score + "\n");
  }
}
