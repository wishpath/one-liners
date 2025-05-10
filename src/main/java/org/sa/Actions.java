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
        .orElseGet(() -> {
          SimpleColorPrint.red("No concepts available.");
          return null;
        });
  }

  public Map.Entry<String, String> pickConceptWithFragmentInKey(String fragment) {
    SimpleColorPrint.blueInLine("Picking key containing fragment ");
    SimpleColorPrint.red(fragment);
    return concepts.map.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .findFirst()
        .orElseGet(() -> {
          SimpleColorPrint.blueInLine("Not found any concept containing fragment: ");
          SimpleColorPrint.red(fragment);
          return pickRandomConcept();
        });
  }


  public Map.Entry<String, String> pickNthKeyDefinition(String fragment, int nthInstance) {
    return concepts.map.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .skip(nthInstance + 1)
        .findFirst()
        .orElseGet(() -> {
          SimpleColorPrint.blueInLine("Not found ");
          SimpleColorPrint.redInLine(String.valueOf(nthInstance));
          SimpleColorPrint.blueInLine("-th concept containing fragment: ");
          SimpleColorPrint.red(fragment);
          return pickRandomConcept();
        });
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

  public void evaluateUserExplanationWithAI(Map.Entry<String, String> concept, String input) {
    SimpleColorPrint.yellow(ai.getAnswer(
        "is this a good keyword and definition: " + concept.getKey() + " = " + input +
            ". \n1 - Evaluate the answer by answering a question \"Does this capture the essence?\" (try to be positive with your evaluation)." +
            "\n if some detail are missing, but it does capture the essence the evaluation should be 10/10" +
            "\n if definition matches this one, rate 10/10: " + concept.getValue() +
            "\n if definition matches this one, rate 10/10: " +
            "\n think how you would formulate an answer in up to 10 words - if you could not comme up with better answer, rate 10/10 " +
            "\n if answer totally totally off, rate 0/10 " +
            "\n if answer somewhat passable, rate 7/10 " +
            "\nand 2 - If evaluations is less than 7/10 - conclude the right answer. Else do not provide the right answer." +
            "\nYour entire answer should be up to 300 characters"));
  }

  public Map.Entry<String, String> pickNthConceptWithFragmentInKey(String input) {
    //pick nth <fragment nth> - pick nth key containing fragment;
    String fragmentToSearchForAndNumber = input.substring("pick nth ".length()); //should be "<fragment nth>"
    String endsWithSpaceDigitsPattern = "^(.*) (\\d+)$";

    if (!fragmentToSearchForAndNumber.matches(endsWithSpaceDigitsPattern)) {
      SimpleColorPrint.blueInLine("pick nth <fragment nth>, ");
      SimpleColorPrint.redInLine(fragmentToSearchForAndNumber);
      SimpleColorPrint.normal(" - did not end with space and number.");
      SimpleColorPrint.red("pick nth <fragment nth> was aborted");
      return pickConceptWithFragmentInKey(input.substring("pick nth ".length()));
    }
    else {
      String fragment = fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$1");
      int nth = Integer.parseInt(fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$2"));
      SimpleColorPrint.blueInLine("Searching for fragment: ");
      SimpleColorPrint.redInLine(fragment );
      SimpleColorPrint.blueInLine(" nth: ");
      SimpleColorPrint.red(String.valueOf(nth));
      return pickNthKeyDefinition(fragment, nth);
    }
  }
}
