package org.sa.dto;

import org.sa.console.SimpleColorPrint;

public class ConceptDTO {
  public String key;
  public String definition;
  public String evaluationInstructions;
  public String userInstructions;
  public Integer score;
  public String instruction;

  public ConceptDTO(String key, String definition) {
    this.key = key;
    this.definition = definition;
    this.evaluationInstructions = "";
    this.userInstructions = "";
    this.score = 0;
    this.instruction = "\nPlease explain this concept: ";
  }

  public void printUserInstruction() {
    SimpleColorPrint.blueInLine(instruction);
    SimpleColorPrint.red(key);
  }
}
