package org.sa.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public static List<File> listFiles(Path path) {
    if (Files.notExists(path) || !Files.isDirectory(path)) return List.of();
    try (Stream<Path> stream = Files.list(path)) {
      return stream.filter(Files::isRegularFile)
          .map(Path::toFile)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> listLines(File file) {
    if (!file.exists() || !file.isFile()) return List.of();
    try {
      return Files.readAllLines(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }



  public static void createTextFileOverwritingly_createPathIfMissing(String path, String content) {
    Path filePath = Path.of(path);
    try {
      if (Files.notExists(filePath.getParent())) Files.createDirectories(filePath.getParent());
      Files.writeString(filePath, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


}
