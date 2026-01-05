package org.sa.util;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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

  public static Stream<Path> listDirectories(Path path) {
    try {
      return Files.list(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Set<Map.Entry<Object, Object>> loadPropertiesFileAsMapEntrySet(Path path) {
    Properties scoreProperties = new Properties();
    try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      scoreProperties.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return scoreProperties.entrySet();
  }

  public static void openInNotepad(String filePath) {
    try { new ProcessBuilder("notepad.exe", filePath).start(); }
    catch (IOException ignored) {}
  }
}
