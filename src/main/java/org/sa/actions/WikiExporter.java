package org.sa.actions;

import org.sa.concepts.Concepts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class WikiExporter {

  private static final Path WIKI_OUTPUT_FILE = Paths.get("src/main/java/org/sa/concepts/wiki.txt");
  private static final String WIKI_INTRO = "*Goal of this article*\nThis collection of super-short definitions captures the core of each concept in just a few words, creating a broad, foundational framework for quick knowledge acquisition. By reducing concepts to their essence—even if imperfect—this approach fosters the confidence needed to deepen understanding later. This minimalist style lets you absorb a wide set of ideas rapidly, forming a scaffold for continuous growth.\n\n";

  public static void main(String[] args) throws IOException {
    Concepts c = new Concepts(); // not injected because this class is a separate app
    printListOfTopics(c);
    exportAllConceptsForWikiPage(c);
  }

  private static void printListOfTopics(Concepts c) throws IOException {
    System.out.println("here");
    Files.walk(c.TOPICS)
        .filter(p -> p.toString().endsWith(".concepts"))
        .forEach(p -> System.out.println(p.getFileName().toString().replace(".concepts", "")));
  }

  private static void exportAllConceptsForWikiPage(Concepts c) throws IOException {
    StringBuilder sb = new StringBuilder(WIKI_INTRO);
    List<Path> wikiTopicFiles = Stream
        .concat(Files.walk(c.TOPICS), Files.walk(c.TOPICS_SWED))
        .filter(p -> p.toString().endsWith(".concepts"))
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
