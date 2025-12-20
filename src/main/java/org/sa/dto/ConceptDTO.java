package org.sa.dto;

import org.sa.console.SimpleColorPrint;

public class ConceptDTO {
  public String key;
  public String definition;
  public String userAnswerInstruction = "\nPlease explain this concept: ";
  public String aiEvaluateInstruction = null;

  public String topic;
  public Integer score;

  public ConceptDTO(String key, String definition) {
    this.key = key;
    this.definition = definition;
    this.score = 0;
  }

  public void printUserInstruction() {
    SimpleColorPrint.blueInLine(userAnswerInstruction);
    SimpleColorPrint.red(key);
  }

  public void printScore() {
    SimpleColorPrint.blueInLine("The score of concept '");
    SimpleColorPrint.redInLine(key);
    SimpleColorPrint.blueInLine("' is: ");
    SimpleColorPrint.red(score + "\n");
  }
}
