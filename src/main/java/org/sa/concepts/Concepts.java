package org.sa.concepts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public class Concepts {
  private static final Path TOPICS = Paths.get("src/main/java/org/sa/concepts/topics");
  private static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");
  public static final Path SCORE_PATH = Paths.get("src/main/java/org/sa/score/score.properties");
  public static final Path WIKI_OUTPUT_FILE = Paths.get("src/main/java/org/sa/concepts/wiki.txt");
  public final Map<String, String> map = new HashMap<>();
  public final Map<String, Integer> score = new HashMap<>();

  public Concepts() throws IOException {

    //load definitions
    for (Path subtopicPath : Files.walk(TOPICS).filter(p -> p.toString().endsWith(".properties")).toList()) {
      Properties props = new Properties();
      props.load(Files.newInputStream(subtopicPath));
      for (Map.Entry<Object, Object> e : props.entrySet())
        map.put(e.getKey().toString(), e.getValue().toString());
    }

    //load scores
    Properties scoreProps = new Properties();
    scoreProps.load(Files.newInputStream(SCORE_PATH));
    for (Map.Entry<Object, Object> e : scoreProps.entrySet())
      score.put(e.getKey().toString(), Integer.parseInt((String)e.getValue()));
  }

  public static void main(String[] args) throws IOException {
    printListOfTopics();
    exportAllConceptsForWiki();
  }

  private static void printListOfTopics() throws IOException {
    Files.walk(TOPICS)
        .filter(p -> p.toString().endsWith(".properties"))
        .forEach(p -> System.out.println(p.getFileName().toString().replace(".properties", "")));
  }

//  private static void exportAllConceptsForWiki() throws IOException {
//    StringBuilder sb = new StringBuilder();
//
//    Stream.concat(Files.walk(TOPICS), Files.walk(TOPICS_SWED))
//        .filter(p -> p.toString().endsWith(".properties"))
//        .forEach(p -> {
//          sb.append(getFileNameWithoutExtension(p)).append("\n");
//          try {
//            Files.lines(p)
//                .filter(line -> line.contains("="))
//                .map(Concepts::transformLine)
//                .forEach(l -> sb.append(l).append("\n"));
//          } catch (IOException e) {}
//        });
//
//    Files.writeString(WIKI_OUTPUT_FILE, sb.toString());
//  }

  private static void exportAllConceptsForWiki() throws IOException {
    StringBuilder sb = new StringBuilder();
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
