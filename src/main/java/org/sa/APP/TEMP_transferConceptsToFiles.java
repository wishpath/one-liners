package org.sa.APP;

import org.sa.a_config.FilePath;
import org.sa.a_config.Props;
import org.sa.util.FileUtil;

import java.io.File;
import java.nio.file.Path;

public class TEMP_transferConceptsToFiles {
  public static void main(String[] args) {
    transfer(FilePath.TOPICS_PUBLIC, FilePath.CONCEPT_FILES_PUBLIC);
    transfer(FilePath.TOPICS_SWED, FilePath.CONCEPT_FILES_SWED);
  }

  private static void transfer(Path inputDirectory, Path outputDirectory) {
    for (File file : FileUtil.listFiles(inputDirectory)) {
      String topicAsPath = file.getName().split("[.]")[0] + "/";
      System.out.println(topicAsPath);
      for (String line : FileUtil.listLines(file)) {
        System.out.println(Props.TAB + line);
        String[] keyDefinition = line.split("[=]", 2);
        if (keyDefinition.length != 2) throw new IllegalStateException("OH NO");
        String key = keyDefinition[0];
        String definition = keyDefinition[1];
        if (key.matches(".*[\\\\/:*?\"<>|].*")) throw new IllegalArgumentException("key should be filename-friendly: " + key);
        key = key.replaceAll("[\\\\/:*?\"<>|]", "");
        System.out.println(key);
        String path = outputDirectory + "\\" +  topicAsPath  + key;
        System.out.println(path);
        String content = keyDefinition[0] + "\n" + keyDefinition[1];

        FileUtil.createTextFileOverwritingly_createPathIfMissing(path, content);
      }
    }
  }
}
