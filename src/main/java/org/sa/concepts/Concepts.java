package org.sa.concepts;

import org.sa.config.Props;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.other.ValueAscendingMap;
import org.sa.service.A_ConceptsLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;


//purpose: load, keep and manage concept info: key, definition, score, status and their permutations
public class Concepts {

  public static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");
  public static final Path SCORE_PATH = Paths.get("src/main/java/org/sa/storage/score.properties");
  public static final Path NOT_TODAY_FILEPATH = Paths.get("src/main/java/org/sa/storage/not_today.csv");

  public final Map<String, ConceptDTO> key_concept = A_ConceptsLoader.loadConceptsCheckRepeated();
  public final ValueAscendingMap<String, Integer> key_score = new ValueAscendingMap<>(); //no keys with score zero, auto ascending
  public final TreeMap<Integer, List<String>> score_keyList = new TreeMap<>(); //auto ascending map
  public final ValueAscendingMap<String, LocalDateTime> notTodayKey_time = new ValueAscendingMap<>();//keys skipped from learning for one day

  public Concepts() throws IOException {
    loadScores();
    applyDefaultScoreZero();
    mapAscendingScoresToConcepts();
    loadNotTodayConcepts();
    System.out.println("Count of concepts: " + key_concept.size());
    System.out.println("Count of scores: " + key_score.size());
  }



  private void loadScores() throws IOException {
    Properties scoreProps = new Properties();
    try (Reader reader = Files.newBufferedReader(SCORE_PATH, StandardCharsets.UTF_8)) {
      scoreProps.load(reader);
    }
    for (Map.Entry<Object, Object> e : scoreProps.entrySet()) {
      if (e.getValue().equals("0")) continue; // 0 is default...
      if (!key_concept.containsKey(e.getKey())) continue; // has score but key got deleted/ altered
      key_score.put(e.getKey().toString(), Integer.parseInt((String)e.getValue()));
    }
  }

  private void applyDefaultScoreZero() {
    //keys not having explicit score, load with score 0
    for (String key : key_concept.keySet())
      if (key_score.get(key) == null)
        score_keyList.computeIfAbsent(0, k -> new ArrayList<>()).add(key);
  }

  private void mapAscendingScoresToConcepts() {
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
      score_keyList.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
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
