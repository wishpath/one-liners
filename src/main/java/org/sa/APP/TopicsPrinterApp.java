package org.sa.APP;

import org.sa.a_config.FilePath;

import java.io.IOException;
import java.nio.file.Files;

public class TopicsPrinterApp {
  //prints filenames, that are topics
  public static void main(String[] args) throws IOException {
    Files.walk(FilePath.TOPICS_PUBLIC)
        .filter(p -> p.toString().endsWith(".concepts"))
        .forEach(p -> System.out.println(p.getFileName().toString().replace(".concepts", "")));
  }
}
