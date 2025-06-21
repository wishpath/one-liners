package org.sa.concepts;

import org.sa.console.SimpleColorPrint;
import org.sa.other.ValueAscendingMap;

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
  public static final Path TOPICS_PUBLIC = Paths.get("src/main/java/org/sa/concepts/topics");
  public static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");
  public static final Path SCORE_PATH = Paths.get("src/main/java/org/sa/score/score.properties");
  public static final Path NOT_TODAY_FILE = Paths.get("src/main/java/org/sa/not_today.csv");

  public final Map<String, String> keyDefinition = new HashMap<>();
  public final ValueAscendingMap<String, Integer> keyScore = new ValueAscendingMap<>(); //no keys with score zero, auto ascending
  public final TreeMap<Integer, List<String>> scoreToKeys = new TreeMap<>(); //auto ascending map
  public final ValueAscendingMap<String, LocalDateTime> notTodayKeys = new ValueAscendingMap<>();//keys skipped from learning for one day

  public Concepts() throws IOException {
    loadConceptsCheckRepeated();
    loadScores();
    applyDefaultScoreZero();
    mapAscendingScoresToConcepts();
    loadNotTodayConcepts();
  }

  private void loadConceptsCheckRepeated() throws IOException {
    for (Path subtopicPath : Files.walk(TOPICS_PUBLIC).filter(p -> p.toString().endsWith(".concepts")).toList())
      Files.lines(subtopicPath)
          .filter(line -> line.contains("="))
          .forEach(line -> {
            String[] arr = line.split("=", 2);
            if(arr[0].contains(";")) throw new RuntimeException("Key '" + arr[0] + "' should not contain semicolon (;)");
            String repeated = keyDefinition.put(arr[0], arr[1]);
            if (repeated != null) {
              SimpleColorPrint.redInLine("The repeated key: ");
              SimpleColorPrint.blueInLine(arr[0]);
              SimpleColorPrint.redInLine(", the definitions: ");
              SimpleColorPrint.blueInLine(arr[1]);
              SimpleColorPrint.redInLine(" and ");
              SimpleColorPrint.blue(repeated);
            }
          });
  }

  private void loadScores() throws IOException {
    Properties scoreProps = new Properties();
    scoreProps.load(Files.newInputStream(SCORE_PATH));
    for (Map.Entry<Object, Object> e : scoreProps.entrySet()) {
      if (e.getValue().equals("0")) continue; // 0 is default...
      if (!keyDefinition.containsKey(e.getKey())) continue; // has score but key got deleted/ altered
      keyScore.put(e.getKey().toString(), Integer.parseInt((String)e.getValue()));
    }
  }

  private void applyDefaultScoreZero() {
    //keys not having explicit score, load with score 0
    for (String key : keyDefinition.keySet())
      if (keyScore.get(key) == null)
        scoreToKeys.computeIfAbsent(0, k -> new ArrayList<>()).add(key);
  }

  private void mapAscendingScoresToConcepts() {
    SimpleColorPrint.red("Current scores:");
    for(Map.Entry<String, Integer> e : keyScore.entrySet()) {
      SimpleColorPrint.blueInLine(e.getKey() + ": ");
      SimpleColorPrint.red(String.valueOf(e.getValue()));
      scoreToKeys.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
    }
  }

  private void loadNotTodayConcepts() throws IOException {
    try (Stream<String> lines = Files.lines(NOT_TODAY_FILE)) {
      LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
      lines.map(line -> line.split(";"))
          .map(parts -> Map.entry(parts[0], LocalDateTime.parse(parts[1])))
          .filter(e -> e.getValue().isAfter(oneDayAgo))
          .forEach(e -> notTodayKeys.put(e.getKey(), e.getValue()));
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void dontLearnThisToday(String key) throws IOException {
    // refresh: remove entries older than one day
    refreshNotTodayMap();

    // add new entry
    notTodayKeys.put(key, LocalDateTime.now());

    //autosave updated list to file
    try (BufferedWriter writer = Files.newBufferedWriter(NOT_TODAY_FILE)) {
      for (Map.Entry<String, LocalDateTime> entry : notTodayKeys.entrySet())
        writer.write(entry.getKey() + ";" + entry.getValue() + System.lineSeparator());
    }
  }

  public void refreshNotTodayMap() {
    LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
    notTodayKeys.entrySet().removeIf(e -> e.getValue().isBefore(oneDayAgo));
  }
}
