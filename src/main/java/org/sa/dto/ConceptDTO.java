package org.sa.dto;

import org.sa.console.SimpleColorPrint;

public class ConceptDTO {
  public String key;
  public String definition;
  public String evaluationInstructions;
  public String userInstructions;
  public Integer score;
  public String answerInstruction;
  public String evaluateInstruction;

  public ConceptDTO(String key, String definition) {
    this.key = key;
    this.definition = definition;
    this.evaluationInstructions = "";
    this.userInstructions = "";
    this.score = 0;
    this.answerInstruction = "\nPlease explain this concept: ";
    this.evaluateInstruction = null;
  }

  public void printUserInstruction() {
    SimpleColorPrint.blueInLine(answerInstruction);
    SimpleColorPrint.red(key);
  }

  public void printScore() {
    SimpleColorPrint.blueInLine("The score of concept '");
    SimpleColorPrint.redInLine(key);
    SimpleColorPrint.blueInLine("' is: ");
    SimpleColorPrint.red(score + "\n");
  }
}
