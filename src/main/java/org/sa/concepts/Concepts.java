package org.sa.concepts;

import org.sa.Prop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Concepts {
  private static final Path DIR = Paths.get("src/main/java/org/sa/concepts/" + Prop.FOLDER);
  public static final Path SCORE_PATH = Paths.get("src/main/java/org/sa/score/score.properties");
  public final Map<String, String> map = new HashMap<>();
  public final Map<String, Integer> score = new HashMap<>();

  public Concepts() throws IOException {
    for (Path subtopicPath : Files.walk(DIR).filter(p -> p.toString().endsWith(".properties")).toList()) {
      Properties props = new Properties();
      props.load(Files.newInputStream(subtopicPath));
      for (Map.Entry<Object, Object> e : props.entrySet())
        map.put(e.getKey().toString(), e.getValue().toString());
    }

    Properties scoreProps = new Properties();
    scoreProps.load(Files.newInputStream(SCORE_PATH));
    for (Map.Entry<Object, Object> e : scoreProps.entrySet())
      score.put(e.getKey().toString(), Integer.parseInt((String)e.getValue()));
  }

  public static void main(String[] args) throws IOException {
    printListOfTopics();
  }

  private static void printListOfTopics() throws IOException {
    Files.walk(DIR)
        .filter(p -> p.toString().endsWith(".properties"))
        .forEach(p -> System.out.println(p.getFileName().toString().replace(".properties", "")));
  }
}
