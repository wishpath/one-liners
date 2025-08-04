package org.sa.actions;

import org.sa.concepts.Concepts;
import org.sa.config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
      SimpleColorPrint.red(fragment + "\n");
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
      SimpleColorPrint.red(fragment + "\n");
      found.forEach(entry -> printConceptWithFragmentHighlighted(entry, fragment));
    }
    System.out.println();
  }

  private static void printConceptWithFragmentHighlighted(Map.Entry<String, String> entry, String fragment) {
    String concept = Props.TAB + entry.getKey() + ": " + entry.getValue();
    printStringWithFragmentHighlighted(fragment, concept, Colors.LIGHT_GRAY, Colors.RED);
  }

  public static void printStringWithFragmentHighlighted(String fragment, String string, String mainColorAnsiCode, String fragmentColorAnsiCode) {
    //lowercase strings for easy finding of index where the fragment is
    String lowerS = string.toLowerCase();
    String lowerFragment = fragment.toLowerCase();

    //each iteration will print up to the end of next matching fragment
    for (int indexOfPrintStart = 0; indexOfPrintStart < string.length(); ) {
     int indexOfFragment = lowerS.indexOf(lowerFragment, indexOfPrintStart);
     if (indexOfFragment == -1) {
       SimpleColorPrint.color(string.substring(indexOfPrintStart), mainColorAnsiCode);
       break;
     }
     //print before fragment
     SimpleColorPrint.colorInLine(string.substring(indexOfPrintStart, indexOfFragment), mainColorAnsiCode);
     //print fragment
     SimpleColorPrint.colorInLine(string.substring(indexOfFragment, indexOfFragment + fragment.length()), fragmentColorAnsiCode);
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
    System.out.println("\n");
  }

  public void printCurrentKeyScore(Map.Entry<String, String> concept) {
    SimpleColorPrint.blueInLine("The score of concept '");
    SimpleColorPrint.redInLine(concept.getKey());
    SimpleColorPrint.blueInLine("' is: ");
    Integer score = concepts.keyScore.get(concept.getKey());
    String finalScore = score == null ? "0" : String.valueOf(score);
    SimpleColorPrint.red(finalScore + "\n");
  }

  public void printAllNonZeroScores() {
    SimpleColorPrint.red("All non-zero scores:");
    for (Map.Entry<String, Integer> e : concepts.keyScore.entrySet()) {
      SimpleColorPrint.blueInLine(Props.TAB + e.getKey() + ": ");
      SimpleColorPrint.red(String.valueOf(e.getValue()));
    }
    SimpleColorPrint.normal("\n\n");
  }

  public void printEntriesWithMinusScore() {
    Set<String> minusScoreKeys = new HashSet<>();
    SimpleColorPrint.blue("Printing concepts with minus score:\n");
    for (Map.Entry<Integer, List<String>> e : concepts.scoreToKeys.entrySet()) {
      if (e.getKey() >= 0) break;
      for (String key : e.getValue()) {
        minusScoreKeys.add(key);
        SimpleColorPrint.redInLine(Props.TAB + key);
        SimpleColorPrint.blueInLine(" - " + concepts.keyDefinition.get(key));
        SimpleColorPrint.normal(" - score: " + concepts.keyScore.get(key));
      }
    }


    SimpleColorPrint.blue("Printing not today concepts (of non-negative score):\n");
    for (Map.Entry<String, LocalDateTime> e : concepts.notTodayKeys.entrySet()) {
      if (minusScoreKeys.contains(e.getKey())) continue;

        SimpleColorPrint.redInLine(Props.TAB + e.getKey());
        SimpleColorPrint.blueInLine(" - " + concepts.keyDefinition.get(e.getKey()));
        Integer score = concepts.keyScore.get(e.getKey());
        if (score == null) score = 0;
        SimpleColorPrint.normal(" - score: " + score);

    }
    System.out.println();
  }

  public void printKeysWithMinusScoreAndDates() {
    SimpleColorPrint.blue("Printing keys with minus score:\n");
    for (Map.Entry<String, LocalDateTime> e : concepts.notTodayKeys.entrySet()) {
      Integer score = concepts.keyScore.get(e.getKey()); // null means score is 0;
      if (score == null || score >= 0) continue;
      String time = e.getValue().plusDays(1).format(DateTimeFormatter.ofPattern("HH:mm"));
      SimpleColorPrint.normalInLine(Props.TAB + time);
      SimpleColorPrint.red(" " + e.getKey());
    }
    System.out.println();
  }
}
