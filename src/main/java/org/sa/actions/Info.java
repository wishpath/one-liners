package org.sa.actions;

import org.sa.concepts.Concepts;
import org.sa.config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.service.NotTodayService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Info {

  private final Concepts concepts;
  private final NotTodayService notTodayService;

  public Info(Concepts concepts, NotTodayService notTodayService) {
    this.concepts = concepts;
    this.notTodayService = notTodayService;
  }

  public void printAllConceptsContainingFragmentInKey(String fragment) {
    List<Map.Entry<String, ConceptDTO>> entryListFound = concepts.key_concept.entrySet().stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (entryListFound.isEmpty()) {
      SimpleColorPrint.redInLine("No keys found containing fragment: ");
      SimpleColorPrint.red(fragment);
    }
    else {
      SimpleColorPrint.blueInLine("Defining all keys containing fragment: ");
      SimpleColorPrint.red(fragment);
      printConceptEntryList_indexed_fragmentHighlighted(fragment, entryListFound);
    }
    System.out.println();
  }

  public void printAllConceptsContainingFragmentInKeyValue(String fragment) {
    List<Map.Entry<String, ConceptDTO>> found = concepts.key_concept.entrySet().stream()
        .filter(entry -> (entry.getKey() + " " + entry.getValue()).toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (found.isEmpty()) {
      SimpleColorPrint.redInLine("No key-values found containing fragment: ");
      SimpleColorPrint.red(fragment);
    }
    else {
      SimpleColorPrint.blueInLine("Defining all key-values containing fragment: ");
      SimpleColorPrint.red(fragment);
      printConceptEntryList_indexed_fragmentHighlighted(fragment, found);
    }
    System.out.println();
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
    concepts.key_concept.entrySet()
        .stream()
        .forEach(entry -> {
          System.out.print(entry.getKey() + ", ");
        });
    System.out.println("\n");
  }

  public void printCurrentKeyScore(ConceptDTO concept) {
    SimpleColorPrint.blueInLine("The score of concept '");
    SimpleColorPrint.redInLine(concept.key);
    SimpleColorPrint.blueInLine("' is: ");
    Integer score = concepts.key_score.get(concept.key);
    String finalScore = score == null ? "0" : String.valueOf(score);
    SimpleColorPrint.red(finalScore + "\n");
  }

  public void printAllNonZeroScores() {
    SimpleColorPrint.red("All non-zero scores:");
    for (Map.Entry<String, Integer> e : concepts.key_score.entrySet()) {
      SimpleColorPrint.blueInLine(Props.TAB + e.getKey() + ": ");
      SimpleColorPrint.red(String.valueOf(e.getValue()));
    }
    SimpleColorPrint.normal("\n\n");
  }

  public void printLowestScoreConcepts() {
    int weakConceptCounter = 0;
    SimpleColorPrint.blue("Printing concepts with lowest score:\n");
    for (Map.Entry<Integer, Set<String>> e : concepts.score_keySet.entrySet()) {
      if (weakConceptCounter > 7) break;
      int score = e.getKey();
      Set<String> keys= e.getValue();
      SimpleColorPrint.red(Props.TAB + score + ":");
      for (String key : keys) {
        if (weakConceptCounter > 11) break;
        weakConceptCounter++;
        SimpleColorPrint.blueInLine(Props.TAB.repeat(2) + key + " ");
        SimpleColorPrint.colorInLine(concepts.key_concept.get(key).definition + " ", Colors.LIGHT_GRAY);
        LocalDateTime notTodayTime = notTodayService.notTodayKey_time.get(key);
        String time = notTodayTime == null ? "" : notTodayTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        System.out.println(time);
      }
      System.out.println();
    }
  }

  public void printNotTodayKeysByTimeAvailableAndLowestScore2() {
    int passableScore = getPassableScore();
    SimpleColorPrint.blue("Printing 'not today' keys with lower scores:\n");
    notTodayService.notTodayKey_time.entrySet().stream()
        .filter(e -> concepts.key_score.getOrDefault(e.getKey(), 0) <= passableScore)
        .forEach(e -> {
          int score = concepts.key_score.getOrDefault(e.getKey(), 0);
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

    for (Map.Entry<Integer, Set<String>> e : concepts.score_keySet.entrySet()) {
      long countAtScore = e.getValue().stream()
          .filter(notTodayService.notTodayKey_time::containsKey)
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
    notTodayService.notTodayKey_time.forEach((key, time) -> {
      String timeString = time.plusDays(1).format(DateTimeFormatter.ofPattern("HH:mm"));
      SimpleColorPrint.redInLine(Props.TAB + timeString);
      SimpleColorPrint.blueInLine(" " + key + " ");
      SimpleColorPrint.color(concepts.key_concept.get(key).definition, Colors.LIGHT_GRAY);
    });
    System.out.println();
  }

  public static void printConceptEntryList_indexed_fragmentHighlighted(String fragment, List<Map.Entry<String, ConceptDTO>> matchingKey_Definition) {
    List<ConceptDTO> concepts = matchingKey_Definition.stream()
        .map(e -> e.getValue())
        .toList();

    SimpleColorPrint.blue("Matching concepts: ");
    for (int i = 0; i < concepts.size(); i++) {
      SimpleColorPrint.normalInLine(Props.TAB + i + " ");
      String concept = Props.TAB + concepts.get(i).key + ": " + concepts.get(i).definition;
      printStringWithFragmentHighlighted(fragment, concept, Colors.LIGHT_GRAY, Colors.RED);
    }
    System.out.println();
  }

  public static void printKeyEntryList_indexed_fragmentHighlighted(String fragment, List<Map.Entry<String, ConceptDTO>> matchingKey_concept) {
    List<ConceptDTO> concepts = matchingKey_concept.stream().map(entry -> entry.getValue()).toList();

    SimpleColorPrint.blue("Matching keys: ");
    for (int i = 0; i < concepts.size(); i++) {
      SimpleColorPrint.normalInLine(Props.TAB + i + " ");
      printStringWithFragmentHighlighted(fragment, Props.TAB + concepts.get(i).key, Colors.LIGHT_GRAY, Colors.RED);
    }
    System.out.println();
  }

  public void printUserInstruction(ConceptDTO c) {
    SimpleColorPrint.blueInLine("\nPlease explain this concept: ");
    SimpleColorPrint.red(c.key);
  }

  public void printAllCurrentScores() {
    SimpleColorPrint.red("Current scores:");
    concepts.key_concept.entrySet().stream()
        .sorted(Comparator.comparingInt(e -> e.getValue().score))
        .forEach(e -> {

          //print: key - blue; not today key - green
          String key = Props.TAB + e.getKey() + ": ";
          if (notTodayService.notTodayKey_time.containsKey(e.getKey()))
            SimpleColorPrint.colorInLine(key, Colors.GREEN);
          else SimpleColorPrint.blueInLine(key);

          //print score
          SimpleColorPrint.red(String.valueOf(e.getValue().score));
        });
    System.out.println();
  }
}
