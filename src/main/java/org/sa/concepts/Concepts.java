package org.sa.concepts;

import org.sa.config.Props;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.other.ValueAscendingMap;
import org.sa.service.loaders.A_ConceptsLoader;
import org.sa.service.loaders.A_ScoresLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;


//purpose: load, keep and manage concept info: key, definition, score, status and their permutations
public class Concepts {

  public static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");

  public static final Path NOT_TODAY_FILEPATH = Paths.get("src/main/java/org/sa/storage/not_today.csv");

  public final Map<String, ConceptDTO> key_concept = A_ConceptsLoader.loadConceptsCheckRepeated();

  //TODO: temporary score structure
  public final Object[] scores = A_ScoresLoader.loadScores(key_concept);
  public final ValueAscendingMap<String, Integer> key_score = (ValueAscendingMap<String, Integer>) scores[0]; //no keys with score zero, auto ascending
  public final TreeMap<Integer, List<String>> score_keyList = (TreeMap<Integer, List<String>>) scores[1]; //auto ascending map

  public final ValueAscendingMap<String, LocalDateTime> notTodayKey_time = new ValueAscendingMap<>();//keys skipped from learning for one day

  public Concepts() throws IOException {
    printAllCurrentScores();
    loadNotTodayConcepts();
    System.out.println("Count of concepts: " + key_concept.size());
    System.out.println("Count of scores: " + key_score.size());
  }

  private void printAllCurrentScores() {
    SimpleColorPrint.red("Current scores:");
    //0 scores
    for (Map.Entry<String, ConceptDTO> e : key_concept.entrySet())
      if (!key_score.containsKey(e.getKey())) {
        SimpleColorPrint.blueInLine(Props.TAB + e.getKey() + ": ");
        SimpleColorPrint.red("0");
      }
    //non 0 scores
    for (Map.Entry<String, Integer> e : key_score.entrySet()) {
      SimpleColorPrint.blueInLine(Props.TAB + e.getKey() + ": ");
      SimpleColorPrint.red(String.valueOf(e.getValue()));
    }
    System.out.println();
  }

  private void loadNotTodayConcepts() throws IOException {

    SimpleColorPrint.blue("LOADING 'NOT TODAY' CONCEPTS:");
    try (Stream<String> lines = Files.lines(NOT_TODAY_FILEPATH)) {
      LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
      lines.map(line -> line.split(","))
          .peek(linePartsArr -> {
            SimpleColorPrint.normal(Props.TAB + Arrays.toString(linePartsArr));
            if (linePartsArr.length > 2) throw new RuntimeException("LINE CONTAINS TOO MANY COMMAS");
          })
          .map(parts -> Map.entry(parts[0], LocalDateTime.parse(parts[1])))
          .filter(e -> e.getValue().isAfter(oneDayAgo))
          .filter(e -> key_concept.containsKey(e.getKey()))
          .forEach(e -> notTodayKey_time.put(e.getKey(), e.getValue()));
    }
    autosaveNotTodayMapToFile();
    System.out.println();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void dontLearnThisToday(String key) throws IOException {
    refreshNotTodayMap(); //remove entries older than one day
    notTodayKey_time.put(key, LocalDateTime.now());
    autosaveNotTodayMapToFile();
  }

  private void autosaveNotTodayMapToFile() throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(NOT_TODAY_FILEPATH)) {
      for (Map.Entry<String, LocalDateTime> entry : notTodayKey_time.entrySet())
        writer.write(entry.getKey() + "," + entry.getValue() + System.lineSeparator()); //overwrites
    }
  }

  public void refreshNotTodayMap() {
    LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
    notTodayKey_time.entrySet().removeIf(e -> e.getValue().isBefore(oneDayAgo));
  }
}
