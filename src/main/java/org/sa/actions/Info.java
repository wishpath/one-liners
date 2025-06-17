package org.sa.actions;

import org.sa.concepts.Concepts;
import org.sa.console.SimpleColorPrint;

import java.util.List;
import java.util.Map;

public class Info {

  private final Concepts concepts;

  public Info(Concepts concepts) {
    this.concepts = concepts;
  }

  public void printAllConceptsContainingFragmentInKey(String fragment) {
    List<Map.Entry<String, String>> found = concepts.keyDefinition.entrySet().stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (found.isEmpty()) {
      SimpleColorPrint.redInLine("No keys found containing fragment: ");
      SimpleColorPrint.red(fragment);
    }
    else {
      SimpleColorPrint.blueInLine("Defining all keys containing fragment: ");
      SimpleColorPrint.red(fragment);
      found.forEach(entry -> printConceptWithFragmentHighlighted(entry, fragment));
    }
    System.out.println();
  }

  public void printAllConceptsContainingFragmentInKeyValue(String fragment) {
    List<Map.Entry<String, String>> found = concepts.keyDefinition.entrySet().stream()
        .filter(entry -> (entry.getKey() + " " + entry.getValue()).toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (found.isEmpty()) {
      SimpleColorPrint.redInLine("No key-values found containing fragment: ");
      SimpleColorPrint.red(fragment);
    }
    else {
      SimpleColorPrint.blueInLine("Defining all key-values containing fragment: ");
      SimpleColorPrint.red(fragment);
      found.forEach(entry -> printConceptWithFragmentHighlighted(entry, fragment));
    }
    System.out.println();
  }

  private static void printConceptWithFragmentHighlighted(Map.Entry<String, String> entry, String fragment) {
    String concept = "\n" + entry.getKey() + ":\n" + entry.getValue() + "\n";

    //lowercase strings just to find index where fragment is
    String lowerConcept = concept.toLowerCase();
    String lowerFragment = fragment.toLowerCase();

    //each iteration will print up to the end of next matching fragment
     for (int indexOfPrintStart = 0; indexOfPrintStart < concept.length(); ) {
      int indexOfFragment = lowerConcept.indexOf(lowerFragment, indexOfPrintStart);
      if (indexOfFragment == -1) {
        SimpleColorPrint.normal(concept.substring(indexOfPrintStart));
        break;
      }
      //print before fragment
      SimpleColorPrint.normalInLine(concept.substring(indexOfPrintStart, indexOfFragment));
      //print fragment
      SimpleColorPrint.redInLine(concept.substring(indexOfFragment, indexOfFragment + fragment.length()));
      indexOfPrintStart = indexOfFragment + fragment.length();
    }
  }

  public void printAllKeys() {
    SimpleColorPrint.blue("Listing all the keys:");
    concepts.keyDefinition.entrySet()
        .stream()
        .forEach(entry -> {
          System.out.print(entry.getKey() + ", ");
        });
    System.out.println();
  }

  public void printCurrentKeyScore(Map.Entry<String, String> concept) {
    SimpleColorPrint.blueInLine("The score of concept '");
    SimpleColorPrint.redInLine(concept.getKey());
    SimpleColorPrint.blueInLine("' is: ");
    Integer score = concepts.keyScore.get(concept.getKey());
    String finalScore = score == null ? "0" : String.valueOf(score);
    SimpleColorPrint.red(finalScore);
  }

  public void printAllNonZeroScores() {
    SimpleColorPrint.red("All non-zero scores:");
    for (Map.Entry<String, Integer> e : concepts.keyScore.entrySet()) {
      SimpleColorPrint.blueInLine(e.getKey() + ": ");
      SimpleColorPrint.red(String.valueOf(e.getValue()));
    }
    SimpleColorPrint.normal("\n\n");
  }

  public void printEntriesWithMinusScore() {
    SimpleColorPrint.blue("Printing concepts with minus score:\n");
    for (Map.Entry<Integer, List<String>> e : concepts.mapScoreToKeys.entrySet()) {
      if (e.getKey() >= 0) break;
      for (String key : e.getValue()) {
        SimpleColorPrint.redInLine(key);
        SimpleColorPrint.blueInLine(" - " + concepts.keyDefinition.get(key));
        SimpleColorPrint.normal(" - score: " + concepts.keyScore.get(key));
      }
    }
    System.out.println();
  }
}
