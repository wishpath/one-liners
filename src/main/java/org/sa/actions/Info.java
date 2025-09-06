package org.sa.actions;

import org.sa.concepts.Concepts;
import org.sa.config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

  public static void printConceptWithFragmentHighlighted(Map.Entry<String, String> entry, String fragment) {
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

  public void printLowestScoreConcepts() {
    int weakConceptCounter = 0;
    SimpleColorPrint.blue("Printing concepts with lowest score:\n");
    for (Map.Entry<Integer, List<String>> e : concepts.scoreToKeys.entrySet()) {
      if (weakConceptCounter > 7) break;
      int score = e.getKey();
      List<String> keys= e.getValue();
      SimpleColorPrint.red(Props.TAB + score + ":");
      for (String key : keys) {
        if (weakConceptCounter > 11) break;
        weakConceptCounter++;
        SimpleColorPrint.blueInLine(Props.TAB.repeat(2) + key + " ");
        SimpleColorPrint.colorInLine(concepts.keyDefinition.get(key) + " ", Colors.LIGHT_GRAY);
        LocalDateTime notTodayTime = concepts.notTodayKeys.get(key);
        String time = notTodayTime == null ? "" : notTodayTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        System.out.println(time);
      }
      System.out.println();
    }
  }

  public void printNotTodayKeysByTimeAvailableAndLowestScore2() {
    int passableScore = getPassableScore();
    SimpleColorPrint.blue("Printing 'not today' keys with lower scores:\n");
    concepts.notTodayKeys.entrySet().stream()
        .filter(e -> concepts.keyScore.getOrDefault(e.getKey(), 0) <= passableScore)
        .forEach(e -> {
          int score = concepts.keyScore.getOrDefault(e.getKey(), 0);
          String time = e.getValue().plusDays(1).format(DateTimeFormatter.ofPattern("HH:mm"));
          SimpleColorPrint.normalInLine(Props.TAB + time);
          SimpleColorPrint.redInLine(" " + e.getKey());
          SimpleColorPrint.color(" " + score, Colors.LIGHT_GRAY);
        });

    System.out.println();
  }

  private int getPassableScore() {
    final int MINIMUM_CONCEPTS_TO_PRINT = 8;
    int passableScore = Integer.MIN_VALUE; // score pointer in order to print at least 8 concepts
    int willBePrinted = 0;

    for (Map.Entry<Integer, List<String>> e : concepts.scoreToKeys.entrySet()) {
      long countAtScore = e.getValue().stream()
          .filter(concepts.notTodayKeys::containsKey)
          .count();
      willBePrinted += countAtScore;

      if (willBePrinted >= MINIMUM_CONCEPTS_TO_PRINT) {
        System.out.println("will be printed: " + willBePrinted);
        passableScore = e.getKey();
        break;
      }
    }
    if (passableScore == Integer.MIN_VALUE) passableScore = Integer.MAX_VALUE; // if fewer than minCount exist, print all
    return passableScore;
  }

  public void printNotTodayConcepts() {
    concepts.notTodayKeys.forEach((key, time) -> {
      String timeString = time.plusDays(1).format(DateTimeFormatter.ofPattern("HH:mm"));
      SimpleColorPrint.redInLine(Props.TAB + timeString);
      SimpleColorPrint.blueInLine(" " + key + " ");
      SimpleColorPrint.color(concepts.keyDefinition.get(key), Colors.LIGHT_GRAY);
    });
    System.out.println();
  }

  public static void printConceptEntryList_indexed_fragmentHighlighted(String fragment, List<Map.Entry<String, String>> matchingKey_Definition) {
    SimpleColorPrint.blue("Matching concepts: ");
    for (int i = 0; i < matchingKey_Definition.size(); i++) {
      SimpleColorPrint.normalInLine(Props.TAB + i + " ");
      Info.printConceptWithFragmentHighlighted(matchingKey_Definition.get(i), fragment);
    }
    System.out.println();
  }
}
