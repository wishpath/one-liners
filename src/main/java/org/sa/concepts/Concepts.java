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

public class Concepts {
  private static final Path TOPICS = Paths.get("src/main/java/org/sa/concepts/topics");
  private static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");
  public static final Path SCORE_PATH = Paths.get("src/main/java/org/sa/score/score.properties");
  public static final Path WIKI_OUTPUT_FILE = Paths.get("src/main/java/org/sa/concepts/wiki.txt");
  public static final Path NOT_TODAY_FILE = Paths.get("src/main/java/org/sa/not_today.csv");
  public static final String WIKI_INTRO = "*Goal of this article*\nThis collection of super-short definitions captures the core of each concept in just a few words, creating a broad, foundational framework for quick knowledge acquisition. By reducing concepts to their essence—even if imperfect—this approach fosters the confidence needed to deepen understanding later. This minimalist style lets you absorb a wide set of ideas rapidly, forming a scaffold for continuous growth.\n\n";

  public final Map<String, String> keyDefinition = new HashMap<>();
  public final ValueAscendingMap<String, Integer> keyScore = new ValueAscendingMap<>(); //no keys with score zero, auto ascending
  public final TreeMap<Integer, List<String>> mapScoreToKeys = new TreeMap<>(); //auto ascending map
  public final Map<String, LocalDateTime> notTodayKeys = new HashMap<>();

  public Concepts() throws IOException {

    loadConceptsCheckRepeated();

    //load scores
    Properties scoreProps = new Properties();
    scoreProps.load(Files.newInputStream(SCORE_PATH));
    for (Map.Entry<Object, Object> e : scoreProps.entrySet()) {
      if (e.getValue().equals("0")) continue; // 0 is default...
      if (!keyDefinition.containsKey(e.getKey())) continue; // has score but key got deleted/ altered
      keyScore.put(e.getKey().toString(), Integer.parseInt((String)e.getValue()));
    }

    //keys not having explicit score, load with score 0
    for (String key : keyDefinition.keySet())
      if (keyScore.get(key) == null)
        mapScoreToKeys.computeIfAbsent(0, k -> new ArrayList<>()).add(key);

    //sort scores ascendingly, list keys for score
    SimpleColorPrint.red("Current scores:");
    for(Map.Entry<String, Integer> e : keyScore.entrySet()) {
      SimpleColorPrint.blueInLine(e.getKey() + ": ");
      SimpleColorPrint.red(String.valueOf(e.getValue()));
      mapScoreToKeys.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
    }

    //load notTodayKeys from file
    try (Stream<String> lines = Files.lines(NOT_TODAY_FILE)) {
      LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
      lines.map(line -> line.split(";"))
          .map(parts -> Map.entry(parts[0], LocalDateTime.parse(parts[1])))
          .filter(e -> e.getValue().isAfter(oneDayAgo))
          .forEach(e -> notTodayKeys.put(e.getKey(), e.getValue()));
    }

  }

  private void loadConceptsCheckRepeated() throws IOException {
    for (Path subtopicPath : Files.walk(TOPICS).filter(p -> p.toString().endsWith(".concepts")).toList())
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


  public static void main(String[] args) throws IOException {
    printListOfTopics();
    exportAllConceptsForWikiPage();
  }

  private static void printListOfTopics() throws IOException {
    Files.walk(TOPICS)
        .filter(p -> p.toString().endsWith(".properties"))
        .forEach(p -> System.out.println(p.getFileName().toString().replace(".properties", "")));
  }

  private static void exportAllConceptsForWikiPage() throws IOException {
    StringBuilder sb = new StringBuilder(WIKI_INTRO);
    List<Path> wikiTopicFiles = Stream
        .concat(Files.walk(TOPICS), Files.walk(TOPICS_SWED))
        .filter(p -> p.toString().endsWith(".properties"))
        .toList();

    for (Path p : wikiTopicFiles) {
      sb.append(getFileNameWithoutExtension(p)); // add title
      for (String line : Files.readAllLines(p))
        sb.append(line.contains("=") ? transformLine(line) : ""); //add line under title
    }

    Files.writeString(WIKI_OUTPUT_FILE, sb.toString());
  }

  private static String getFileNameWithoutExtension(Path p) {
    String baseName = p.getFileName().toString().replace(".properties", "");
    String spaced = baseName.replaceAll("([a-z])([A-Z])", "$1 $2");
    return "\n*" + Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1) + "*\n";
  }

  private static String transformLine(String line) {
    String[] parts = line.split("=", 2);
    String key = parts[0].replace("\\ ", " ");
    String value = parts.length > 1 ? parts[1].trim() : "";
    return "     " + key + " - " + value + "\n";
  }

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
