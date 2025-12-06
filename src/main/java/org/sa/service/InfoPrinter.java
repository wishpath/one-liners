package org.sa.service;

import org.sa.a_config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.util.StringConsoleUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class InfoPrinter {

  private final ConceptsLoader concepts;
  private final NotTodayService notTodayService;

  public InfoPrinter(ConceptsLoader concepts, NotTodayService notTodayService) {
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
      printConcepts_fragmentHighlighted(fragment, entryListFound);
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
      printConcepts_fragmentHighlighted(fragment, found);
    }
    System.out.println();
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

  public void printNotTodayConcepts() {
    notTodayService.notTodayKey_time.forEach((key, time) -> {
      String timeString = time.plusDays(1).format(DateTimeFormatter.ofPattern("HH:mm"));
      SimpleColorPrint.redInLine(Props.TAB + timeString);
      SimpleColorPrint.blueInLine(" " + key + " ");
      SimpleColorPrint.color(concepts.key_concept.get(key).definition, Colors.LIGHT_GRAY);
    });
    System.out.println();
  }

  public static void printConcepts_fragmentHighlighted(String fragment, List<Map.Entry<String, ConceptDTO>> conceptEntries) {
    List<ConceptDTO> concepts = conceptEntries.stream().map(e -> e.getValue()).toList();
    SimpleColorPrint.blue("Matching concepts: ");
    for (int i = 0; i < concepts.size(); i++) {
      SimpleColorPrint.normalInLine(Props.TAB + i + " ");
      String concept = Props.TAB + concepts.get(i).key + ": " + concepts.get(i).definition;
      StringConsoleUtil.printStringWithFragmentHighlighted(fragment, concept, Colors.LIGHT_GRAY, Colors.RED);
    }
    System.out.println();
  }

  public static void printKeys_fragmentHighlighted(String fragment, List<Map.Entry<String, ConceptDTO>> conceptEntries) {
    List<ConceptDTO> concepts = conceptEntries.stream().map(entry -> entry.getValue()).toList();

    SimpleColorPrint.blue("Matching keys: ");
    for (int i = 0; i < concepts.size(); i++) {
      SimpleColorPrint.normalInLine(Props.TAB + i + " ");
      StringConsoleUtil.printStringWithFragmentHighlighted(fragment, Props.TAB + concepts.get(i).key, Colors.LIGHT_GRAY, Colors.RED);
    }
    System.out.println();
  }

  public void printAllCurrentScores() {
    SimpleColorPrint.red("Current scores:");
    concepts.key_concept.entrySet().stream()
        .sorted(Comparator.comparingInt(e -> e.getValue().score))
        .forEach(e -> {

          //print: key - blue; not today key - green
          String keyFormatted = Props.TAB + e.getKey() + ": ";
          if (notTodayService.notTodayKey_time.containsKey(e.getKey()))
            SimpleColorPrint.colorInLine(keyFormatted, Colors.GREEN);
          else SimpleColorPrint.blueInLine(keyFormatted);

          //print score
          SimpleColorPrint.redInLine(String.valueOf(e.getValue().score));

          //print not today time
          LocalDateTime notTodayTime = notTodayService.notTodayKey_time.get(e.getKey());
          if (notTodayTime != null) {
            String time = notTodayTime.plusDays(1).format(DateTimeFormatter.ofPattern(" HH:mm"));
            SimpleColorPrint.colorInLine( time, Colors.LIGHT_GRAY);

            //print definition
            if (LocalDateTime.now().isBefore(notTodayTime.plusHours(23))) {
              SimpleColorPrint.yellowInLine(" " + e.getValue().definition);
            }
          }

          System.out.println();
        });
    System.out.println();
  }
}
