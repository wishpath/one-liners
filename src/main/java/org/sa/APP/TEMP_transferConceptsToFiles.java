package org.sa.APP;

import org.sa.a_config.FilePath;
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

      //get topic
      String topic = file.getName().split("[.]")[0];
      System.out.println("topic that we get from filename: " + topic);

      for (String lineAsConcept : FileUtil.listLines(file)) {
        //get concept
        String[] keyDefinition = lineAsConcept.split("[=]", 2);
        if (keyDefinition.length != 2) throw new IllegalStateException("OH NO");

        //get key
        String key = keyDefinition[0];
        if (key.matches(".*[\\\\/:*?\"<>|].*")) throw new IllegalArgumentException("key should be filename-friendly: " + key);

        //get concept lines:
        String definition = keyDefinition[1];
        String userAnswerInstruction = "Please explain this concept: ";
        String aiEvaluateInstruction = "Default aiEvaluateInstruction";

        //write to file
        String path = outputDirectory + "\\" +  topic  + "\\" +  key;
        String fileContent
            = key + "\n"
            + definition + "\n"
            + userAnswerInstruction + "\n"
            + aiEvaluateInstruction + "\n"
            + topic;
        FileUtil.createTextFileOverwritingly_createPathIfMissing(path, fileContent);
      }
    }
  }
}
