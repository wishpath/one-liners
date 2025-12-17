package org.sa.APP;

import org.sa.a_config.FilePath;
import org.sa.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class WikiExporterAPP {
  private static final String WIKI_INTRO_SIMPLE
      = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
      + "\n*Goal of this article*"
      + "\nThis set of very short definitions gets straight to the point, "
      + "giving you the basics of each idea in just a few words. "
      + "It’s a fast way to start learning and build confidence, even if the definitions aren’t perfect. "
      + "The simple style helps you pick up many ideas quickly and gives you a base to learn more over time.\n\n";

  public static void main(String[] args) throws IOException {
    StringBuilder sb_swed_wiki_text = new StringBuilder(WIKI_INTRO_SIMPLE);
    StringBuilder sb_public_wiki_text = new StringBuilder(WIKI_INTRO_SIMPLE);

    List<Path> swedTopicFiles = Files.walk(FilePath.TOPICS_SWED).filter(p -> p.toString().endsWith(".concepts")).toList();
    List<Path> publicTopicFiles = Files.walk(FilePath.TOPICS_PUBLIC).filter(p -> p.toString().endsWith(".concepts")).toList();

    addPublicTopicsToBothWikis(publicTopicFiles, sb_swed_wiki_text, sb_public_wiki_text);
    addSwedTopicsToSwedWikiOnly(swedTopicFiles, sb_swed_wiki_text);

    FileUtil.overwriteToFileAndCreatePath(FilePath.WIKI_SWED_OUTPUT_FILE, sb_swed_wiki_text);
    FileUtil.overwriteToFileAndCreatePath(FilePath.WIKI_PUBLIC_OUTPUT_FILE, sb_public_wiki_text);
  }

  private static void addPublicTopicsToBothWikis(List<Path> publicTopicFiles, StringBuilder sb_swed, StringBuilder sb_public) throws IOException {
    for (Path p : publicTopicFiles) {
      sb_swed.append(getFileNameWithoutExtension(p)); // add title
      sb_public.append(getFileNameWithoutExtension(p)); // add title
      for (String line : Files.readAllLines(p)) {
        sb_swed.append(line.contains("=") ? transformLine(line) : ""); //add line under title
        sb_public.append(line.contains("=") ? transformLine(line) : ""); //add line under title
      }
    }
  }

  private static void addSwedTopicsToSwedWikiOnly(List<Path> swedTopicFiles, StringBuilder sb_swed) throws IOException {
    for (Path p : swedTopicFiles) {
      sb_swed.append(getFileNameWithoutExtension(p)); // add title
      for (String line : Files.readAllLines(p)) {
        sb_swed.append(line.contains("=") ? transformLine(line) : ""); //add line under title
      }
    }
  }

  private static String getFileNameWithoutExtension(Path p) {
    String baseName = p.getFileName().toString().replace(".concepts", "");
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
