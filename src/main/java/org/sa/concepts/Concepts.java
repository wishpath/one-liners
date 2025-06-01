package org.sa.concepts;

import org.sa.console.SimpleColorPrint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Concepts {
  private static final Path TOPICS = Paths.get("src/main/java/org/sa/concepts/topics");
  private static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");
  public static final Path SCORE_PATH = Paths.get("src/main/java/org/sa/score/score.properties");
  public static final Path WIKI_OUTPUT_FILE = Paths.get("src/main/java/org/sa/concepts/wiki.txt");
  public static final String WIKI_INTRO = "*Goal of this article*\nThis collection of super-short definitions captures the core of each concept in just a few words, creating a broad, foundational framework for quick knowledge acquisition. By reducing concepts to their essence—even if imperfect—this approach fosters the confidence needed to deepen understanding later. This minimalist style lets you absorb a wide set of ideas rapidly, forming a scaffold for continuous growth.\n\n";

  public final Map<String, String> keyDefinition = new HashMap<>();
  public final Map<String, Integer> keyScore = new HashMap<>(); //no keys with score zero
  public final TreeMap<Integer, List<String>> mapScoreToKeys = new TreeMap<>(); //auto ascending map

  public Concepts() throws IOException {

    //load definitions
    for (Path subtopicPath : Files.walk(TOPICS).filter(p -> p.toString().endsWith(".properties")).toList()) {
      Properties props = new Properties();
      props.load(Files.newInputStream(subtopicPath));
      for (Map.Entry<Object, Object> e : props.entrySet())
        keyDefinition.put(e.getKey().toString(), e.getValue().toString());
    }

    //load scores
    Properties scoreProps = new Properties();
    scoreProps.load(Files.newInputStream(SCORE_PATH));
    for (Map.Entry<Object, Object> e : scoreProps.entrySet())
      if (!e.getValue().equals("0"))
        keyScore.put(e.getKey().toString(), Integer.parseInt((String)e.getValue()));

    //keys not having explicit score, load with score 0
    for (String key : keyDefinition.keySet())
      if (keyScore.get(key) == null)
        mapScoreToKeys.computeIfAbsent(0, k -> new ArrayList<>()).add(key);

    //sort scores ascendingly, list keys for score
    SimpleColorPrint.blue("Current scores:");
    for(Map.Entry<String, Integer> e : keyScore.entrySet()) {
      SimpleColorPrint.blueInLine(e.getKey() + ": ");
      SimpleColorPrint.red(String.valueOf(e.getValue()));
      mapScoreToKeys.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
    }

  }

  public void incrementScore(String key, int increment) {
    Integer initialScore = keyScore.get(key);
    if (initialScore == null) initialScore = 0;
    keyScore.merge(key, increment, Integer :: sum);
    int finalScore = keyScore.get(key);
    if (finalScore == 0) keyScore.remove(key);

    List<String> initialList = mapScoreToKeys.get(initialScore);
    if (initialList.size() == 1) mapScoreToKeys.remove(initialScore);
    else initialList.remove(key);


    mapScoreToKeys.computeIfAbsent(finalScore, k -> new ArrayList<>()).add(key);
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
}
