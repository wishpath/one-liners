package org.sa.dto;

import java.util.List;

public class ConceptDTO {
  public String key;
  public String definition;
  public List<String> evaluationInstructions;
  public List<String> userInstructions;

  public ConceptDTO(String key, String definition, List<String> evaluationInstructions, List<String> userInstructions) {
    this.key = key;
    this.definition = definition;
    this.evaluationInstructions = evaluationInstructions;
    this.userInstructions = userInstructions;
  }
}
