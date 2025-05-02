package org.sa;

import org.sa.concepts.Concepts;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class Function {

  private Concepts concepts = new Concepts();
  private AiClient ai = new AiClient();

  public Function() throws IOException {
  }

  public Map.Entry<String, String> pickRandomConcept() {
    return concepts.map.entrySet()
        .stream()
        .skip(new Random().nextInt(concepts.map.size()))
        .findFirst()
        .orElse(null);
  }

  public Map.Entry<String, String> pickConceptWithFragmentInKey(String fragment) {
    return concepts.map.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .findFirst()
        .orElse(pickRandomConcept());
  }


  public Map.Entry<String, String> pickNthKeyDefinition(String fragment, int nthInstance) {
    return concepts.map.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .skip(nthInstance + 1)
        .findFirst()
        .orElse(pickRandomConcept());
  }

  public void printAllConceptsContainingFragmentInKey(String fragment) {
    System.out.println("defining all keys containing fragment: " + fragment);
    concepts.map.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .forEach(entry -> {
          System.out.println("\n" + entry.getKey() + ":");
          System.out.println(entry.getValue());
        });
    System.out.println();
  }

  public void printAllConceptsContainingFragmentInKeyValue(String fragment) {
    System.out.println("defining all key-values containing fragment: " + fragment);
    concepts.map.entrySet()
        .stream()
        .filter(entry -> (entry.getKey() + " " + entry.getValue()).toLowerCase().contains(fragment.toLowerCase()))
        .forEach(entry -> {
          System.out.println("\n" + entry.getKey() + ":");
          System.out.println(entry.getValue());
        });
    System.out.println();
  }

  public void printAllKeys() {
    System.out.println("The list of all keys:");
    concepts.map.entrySet()
        .stream()
        .forEach(entry -> {
          System.out.print(entry.getKey() + ", ");
        });
    System.out.println();
  }

  public void askAi(String input) {
    System.out.println(ai.getAnswer(input));
  }

  public void evaluateUserExplanationWithAI(String key, String input) {
    System.out.println(ai.getAnswer(
        "is this a good keyword and definition: " + key + " = " + input +
            ". \n1 - Evaluate the answer by answering a question \"Does this capture the essence?\" (keeping in mind, that definition is allowed to be concise, minimalistic and does not require details). If, it does capture the essence the evaluation should be 8/10 and above." +
            "\nand 2 - If evaluations is less than 7/10 - conclude the right answer. Else do not provide the right answer." +
            "\nYour entire answer should be up to 300 characters"));
  }

  public Map.Entry<String, String> pickNthConceptWithFragmentInKey(String input) {
    String fragmentToSearchForAndNumber = input.substring("pick nth ".length());
    String endsWithSpaceDigitsPattern = "^(.*) (\\d+)$";

    if (!fragmentToSearchForAndNumber.matches(endsWithSpaceDigitsPattern)) {
      System.out.println("no match");
      return pickConceptWithFragmentInKey(input.substring("pick nth ".length()));
    }
    else {
      String fragment = fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$1");
      int nth = Integer.parseInt(fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$2"));
      System.out.println(fragment + " " + nth);
      return pickNthKeyDefinition(fragment, nth);
    }
  }
}
