package org.sa;

import org.sa.concepts.Concepts;
import org.sa.console.SimpleColorPrint;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class Actions {

  private Concepts concepts = new Concepts();
  private AiClient ai = new AiClient();

  public Actions() throws IOException {
  }

  public Map.Entry<String, String> pickRandomConcept() {
    SimpleColorPrint.blue("Picking a random concept...");
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
    SimpleColorPrint.blueInLine("defining all keys containing fragment: ");
    SimpleColorPrint.red(fragment);
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
    SimpleColorPrint.blueInLine("defining all key-values containing fragment: ");
    SimpleColorPrint.red(fragment);
    concepts.map.entrySet()
        .stream()
        .filter(entry -> (entry.getKey() + " " + entry.getValue()).toLowerCase().contains(fragment.toLowerCase()))
        .forEach(entry -> {
          System.out.println("\n" + entry.getKey() + ":");
          System.out.println(entry.getValue());
        });
    System.out.println("");
  }

  public void printAllKeys() {
    SimpleColorPrint.blue("Listing all the keys:");
    concepts.map.entrySet()
        .stream()
        .forEach(entry -> {
          System.out.print(entry.getKey() + ", ");
        });
    System.out.println();
  }

  public void askAi(String input) {
    SimpleColorPrint.yellow(ai.getAnswer(input));
  }

  public void evaluateUserExplanationWithAI(String key, String input) {
    SimpleColorPrint.yellow(ai.getAnswer(
        "is this a good keyword and definition: " + key + " = " + input +
            ". \n1 - Evaluate the answer by answering a question \"Does this capture the essence?\" (keeping in mind, that definition is allowed to be concise, minimalistic and does not require details). If, it does capture the essence the evaluation should be 8/10 and above." +
            "\nand 2 - If evaluations is less than 7/10 - conclude the right answer. Else do not provide the right answer." +
            "\nYour entire answer should be up to 300 characters"));
  }

  public Map.Entry<String, String> pickNthConceptWithFragmentInKey(String input) {
    //pick nth <fragment nth> - pick nth key containing fragment;
    String fragmentToSearchForAndNumber = input.substring("pick nth ".length()); //should be "<fragment nth>"
    String endsWithSpaceDigitsPattern = "^(.*) (\\d+)$";

    if (!fragmentToSearchForAndNumber.matches(endsWithSpaceDigitsPattern)) {
      SimpleColorPrint.red("pick nth <fragment nth>");
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
