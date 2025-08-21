package org.sa.apps;

import org.sa.config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.other.ValueAscendingMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


//loads keyDefinition and keyScore from files independently from main app.
public class CheckConceptsAttempts {

  public final Map<String, String> keyDefinition = loadConceptsCheckRepeated();
  public final ValueAscendingMap<String, Integer> keyScore = loadScores(); //no keys with score zero, auto ascending


  record AttemptRecord(String key, String definition, int evaluation, LocalDateTime timestamp) {}

  public static void main(String[] args) {
    CheckConceptsAttempts app = new CheckConceptsAttempts();
    List<AttemptRecord> attempts = readAttempts();
    Map<String, List<AttemptRecord>> key_attempts = app.groupAttemptsByExistingKey(attempts); // filters out keys that are not present in 'keyDefinition'
    //app.printGroupedAttempts(key_attempts);
    app.printKeyGrades(key_attempts, 8);
    //app.printAttemptsByAlphabeticalOrderOfKeys(attempts);
  }

  private static List<AttemptRecord> readAttempts() {
    final Path ATTEMPTED_ANSWERS_FILEPATH = Paths.get("src/main/java/org/sa/data/attempted_answers.csv");
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

  public void printAttemptsByAlphabeticalOrderOfKeys(List<AttemptRecord> attempts) {
    attempts.stream()
        .sorted(Comparator.comparing(AttemptRecord::key, String.CASE_INSENSITIVE_ORDER))
        .forEach(this::printSingleAttempt);
  }

  private void printSingleAttempt(AttemptRecord a) {
    if (!keyDefinition.containsKey(a.key())) {
      SimpleColorPrint.red(a.key() + " | " + a.definition() + " | " + a.evaluation() + " | " + a.timestamp());
    } else {
      SimpleColorPrint.blueInLine(a.key());
      SimpleColorPrint.normalInLine(" | ");
      SimpleColorPrint.yellowInLine(a.definition());
      SimpleColorPrint.normalInLine(" | ");
      SimpleColorPrint.redInLine(String.valueOf(a.evaluation()));
      SimpleColorPrint.normalInLine(" | ");
      SimpleColorPrint.normal(a.timestamp().toString());
    }
  }

  private void printSingleAttemptWithoutKey(AttemptRecord a) {
    SimpleColorPrint.blueInLine(Props.TAB.repeat(2) + String.valueOf(a.evaluation()));
    SimpleColorPrint.normalInLine(" | ");
    SimpleColorPrint.yellowInLine(a.definition());
    SimpleColorPrint.normalInLine(" | ");
    SimpleColorPrint.normal(a.timestamp().toString());
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

  public Map<String, List<AttemptRecord>> groupAttemptsByExistingKey(List<AttemptRecord> attempts) {
    return attempts.stream()
        .filter(a -> keyDefinition.containsKey(a.key()))
        .collect(Collectors.groupingBy(AttemptRecord::key));
  }

  public void printGroupedAttempts(Map<String, List<AttemptRecord>> keyAttempts) {
    keyAttempts.forEach((key, attempts) -> {
      String score = keyScore.containsKey(key) ? Integer.toString(keyScore.get(key)) : "0";
      SimpleColorPrint.red(key + ": ");
      SimpleColorPrint.normal(Props.TAB + Colors.LIGHT_GRAY + "score: " + score + Colors.RESET);
      attempts.forEach(this::printSingleAttemptWithoutKey);
      System.out.println();
    });
  }

  private ValueAscendingMap<String, Integer> loadScores(){
    ValueAscendingMap<String, Integer> keyScoreX = new ValueAscendingMap<>(); //no keys with score zero, auto ascending
    final Path SCORE_PATH = Paths.get("src/main/java/org/sa/score/score.properties");
    Properties scoreProps = new Properties();
    try (Reader reader = Files.newBufferedReader(SCORE_PATH, StandardCharsets.UTF_8)) {
      scoreProps.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    for (Map.Entry<Object, Object> e : scoreProps.entrySet()) {
      if (e.getValue().equals("0")) continue; // 0 is default...
      if (!keyDefinition.containsKey(e.getKey())) continue; // has score but key got deleted/ altered
      keyScoreX.put(e.getKey().toString(), Integer.parseInt((String)e.getValue()));
    }

    return keyScoreX;
  }

  public void printKeyGrades(Map<String, List<AttemptRecord>> keyAttempts) {
    keyAttempts.forEach((key, records) -> {
      SimpleColorPrint.redInLine(key + ": ");
      String grades = records.stream()
          .map(r -> String.valueOf(r.evaluation()))
          .collect(Collectors.joining(", "));
      SimpleColorPrint.blue(grades);
    });
  }

  public void printKeyGrades(Map<String, List<AttemptRecord>> keyAttempts, int belowThisGrade) {
    keyAttempts.forEach((key, records) -> {
      boolean allBelow = records.stream().allMatch(r -> r.evaluation() < belowThisGrade);
      if (allBelow) {
        SimpleColorPrint.redInLine(key + ": ");
        String grades = records.stream()
            .map(r -> String.valueOf(r.evaluation()))
            .collect(Collectors.joining(", "));
        SimpleColorPrint.blue(grades);
      }
    });
  }

}
