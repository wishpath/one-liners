package org.sa.dto;

public class ConceptDTO {
  public String key;
  public String definition;
  public String evaluationInstructions;
  public String userInstructions;
  public Integer score;

  public ConceptDTO(String key, String definition) {
    this.key = key;
    this.definition = definition;
    this.evaluationInstructions = "";
    this.userInstructions = "";
    this.score = 0;
  }
}
