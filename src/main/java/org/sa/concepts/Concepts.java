package org.sa.concepts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Concepts {
  private static final Path DIR = Paths.get("src/main/java/org/sa/concepts/topics");
  public final Map<String, String> map = new HashMap<>();

  public Concepts() throws IOException {
    for (Path subtopicPath : Files.walk(DIR).filter(p -> p.toString().endsWith(".properties")).toList()) {
      Properties props = new Properties();
      props.load(Files.newInputStream(subtopicPath));
      for (Map.Entry<Object, Object> e : props.entrySet())
        map.put(e.getKey().toString(), e.getValue().toString());
    }
  }

  public static void main(String[] args) throws IOException {
    Files.walk(DIR)
        .filter(p -> p.toString().endsWith(".properties"))
        .forEach(p -> System.out.println(p.getFileName().toString().replace(".properties", "")));
  }
}
