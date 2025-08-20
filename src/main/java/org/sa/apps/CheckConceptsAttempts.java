package org.sa.apps;

import org.sa.console.SimpleColorPrint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CheckConceptsAttempts {
  private static final Path ATTEMPTED_ANSWERS_FILEPATH = Paths.get("src/main/java/org/sa/data/attempted_answers.csv");
  public final Map<String, String> keyDefinition = loadConceptsCheckRepeated();

  record AttemptRecord(String concept, String definition, int evaluation, LocalDateTime timestamp) {}

  public static void main(String[] args) {
    CheckConceptsAttempts app = new CheckConceptsAttempts();
    List<AttemptRecord> attempts = readAttempts();
    //app.printAttemptsByOrderOfFile(attempts);
    System.out.println("\n--- Sorted Alphabetically ---\n");
    app.printAttemptsByAlphabeticalOrderOfKeys(attempts);
  }

  private static List<AttemptRecord> readAttempts() {
    try (BufferedReader reader = Files.newBufferedReader(ATTEMPTED_ANSWERS_FILEPATH)) {
      return reader.lines()
          .map(CheckConceptsAttempts::parseLine)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static AttemptRecord parseLine(String line) {
    String[] parts = line.split(",", 4);
    if (parts.length < 4) return null;
    String concept = parts[0];
    String definition = parts[1];
    int evaluation = Integer.parseInt(parts[2]);
    LocalDateTime timestamp = LocalDateTime.parse(parts[3]);
    return new AttemptRecord(concept, definition, evaluation, timestamp);
  }

  private void printAttemptsByOrderOfFile(List<AttemptRecord> attempts) {
    attempts.forEach(this::printSingleAttempt);
  }

  public void printAttemptsByAlphabeticalOrderOfKeys(List<AttemptRecord> attempts) {
    attempts.stream()
        .sorted(Comparator.comparing(AttemptRecord::concept, String.CASE_INSENSITIVE_ORDER))
        .forEach(this::printSingleAttempt);
  }

  private void printSingleAttempt(AttemptRecord a) {
    if (!keyDefinition.containsKey(a.concept())) {
      SimpleColorPrint.red(a.concept() + " | " + a.definition() + " | " + a.evaluation() + " | " + a.timestamp());
    } else {
      SimpleColorPrint.blueInLine(a.concept());
      SimpleColorPrint.normalInLine(" | ");
      SimpleColorPrint.yellowInLine(a.definition());
      SimpleColorPrint.normalInLine(" | ");
      SimpleColorPrint.redInLine(String.valueOf(a.evaluation()));
      SimpleColorPrint.normalInLine(" | ");
      SimpleColorPrint.normal(a.timestamp().toString());
    }
  }

  private Map<String, String> loadConceptsCheckRepeated() {
    final Path TOPICS_PUBLIC = Paths.get("src/main/java/org/sa/concepts/topics");
    Map<String, String> keyDefinitionX = new HashMap<>();
    try {
      for (Path subtopicPath : Files.walk(TOPICS_PUBLIC).filter(p -> p.toString().endsWith(".concepts")).toList())
        Files.lines(subtopicPath)
            .filter(line -> line.contains("="))
            .forEach(line -> {
              String[] arr = line.split("=", 2);
              if (arr[0].contains(";")) throw new RuntimeException("Key '" + arr[0] + "' should not contain semicolons (;)");
              if (arr[0].contains(",")) throw new RuntimeException("Key '" + arr[0] + "' should not contain commas (,)");
              String repeated = keyDefinitionX.put(arr[0], arr[1]);
              if (repeated != null) {
                SimpleColorPrint.redInLine("The repeated key: ");
                SimpleColorPrint.blueInLine(arr[0]);
                SimpleColorPrint.redInLine(", the definitions: ");
                SimpleColorPrint.blueInLine(arr[1]);
                SimpleColorPrint.redInLine(" and ");
                SimpleColorPrint.blue(repeated);
              }
            });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return keyDefinitionX;
  }
}
