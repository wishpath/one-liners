package org.sa.APP;

import org.sa.a_config.FilePath;

import java.nio.file.Files;

public class TopicsPrinterApp {
  public static void main(String[] args) throws Exception {
    Files.list(FilePath.CONCEPT_FILES)
        .filter(Files::isDirectory)
        .forEach(p -> System.out.println(p.getFileName()));
  }
}
