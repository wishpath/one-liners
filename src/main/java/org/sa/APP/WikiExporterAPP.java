package org.sa.APP;

import org.sa.a_config.FilePath;
import org.sa.dto.ConceptDTO;
import org.sa.service.ConceptsLoader;
import org.sa.util.FileUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class WikiExporterAPP {

  private static final String WIKI_INTRO_TEXT =
      LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
          "\n*Goal of this article*" +
          "\nThis set of very short definitions gets straight to the point, " +
          "giving you the basics of each idea in just a few words. " +
          "It’s a fast way to start learning and build confidence, even if the definitions aren’t perfect. " +
          "The simple style helps you pick up many ideas quickly and gives you a base to learn more over time.\n\n";

  public static void main(String[] args) {
    StringBuilder wikiTextBuilder = new StringBuilder(WIKI_INTRO_TEXT);

    Map<String, List<ConceptDTO>> key_concepts = new ConceptsLoader().key_concept.values().stream()
        .collect(Collectors.groupingBy(concept -> concept.topic));

    key_concepts = new TreeMap<>(key_concepts);

    key_concepts.forEach((key, value) -> {
      //topic
      String topicTitleWithSpaces = key.replaceAll("([a-z])([A-Z])", "$1 $2");
      System.out.println("Topic: " + topicTitleWithSpaces);

      //add topic with first upper case letter
      wikiTextBuilder.append("\n*")
          .append(Character.toUpperCase(topicTitleWithSpaces.charAt(0)))
          .append(topicTitleWithSpaces.substring(1))
          .append("*\n");

      //add each concept in lines
      value.stream().sorted(Comparator.comparing(concept -> concept.key))
          .forEach(concept ->
              wikiTextBuilder.append("     ")
                  .append(concept.key)
                  .append(" - ")
                  .append(concept.definition)
                  .append("\n"));
    });

    FileUtil.overwriteToFileAndCreatePath(FilePath.WIKI, wikiTextBuilder);
    FileUtil.openInNotepad(FilePath.WIKI.toAbsolutePath().toString());
  }
}
