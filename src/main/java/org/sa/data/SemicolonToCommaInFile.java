package org.sa.data;
import java.nio.file.*;
import java.io.IOException;

public class SemicolonToCommaInFile {
  public static void main(String[] args) throws IOException {
    Path in = Paths.get("src/main/java/org/sa/data/attempted_answers.csv");
    Path out = Paths.get("src/main/java/org/sa/data/attempted_answers_1.csv");
    Files.write(out, Files.readAllLines(in).stream()
        .map(line -> line.replace(",", "#").replace(";", ",").replace("#", ";"))
        .toList());
  }
}
