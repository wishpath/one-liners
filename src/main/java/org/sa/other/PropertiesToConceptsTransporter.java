package org.sa.other;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PropertiesToConceptsTransporter {
  private static final Path TOPICS = Paths.get("src/main/java/org/sa/concepts/topics");
  private static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");
  private static final Path DICTIONARY = Paths.get("src/main/java/org/sa/concepts/dictionary");

  public static void main(String[] args) throws IOException {
    transport(TOPICS);
    transport(TOPICS_SWED);
    transport(DICTIONARY);
  }

  private static void transport(Path path) throws IOException {
    for (Path p : Files.walk(path).filter(p -> p.toString().endsWith(".properties")).toList()) {
      Properties props = new Properties();

      //props.load(Files.newInputStream(p)); //was replaced to solve correct lithuanian letters
      try (var reader = Files.newBufferedReader(p, java.nio.charset.StandardCharsets.UTF_8)) {
        props.load(reader);
      }

      Path newPath = p.getParent().resolve(p.getFileName().toString().replaceAll("\\.properties$", ".concepts"));
      BufferedWriter writer = Files.newBufferedWriter(newPath);

      List<Map.Entry<Object, Object>> sortedList = new ArrayList<>(props.entrySet());
      Collections.sort(sortedList, Comparator.comparing(e -> e.getKey().toString()));

      for (Map.Entry<Object, Object> e : sortedList)
        writer.write(e.getKey() + "=" + e.getValue() + "\n");

      writer.flush();
      writer.close();
    }
  }
}
