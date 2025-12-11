package org.sa.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
  public static void overwriteToFileAndCreatePath(Path path, StringBuilder text) {
    try {
      if (Files.notExists(path.getParent())) {
        Files.createDirectories(path.getParent());
      }
      Files.writeString(path, text.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
