package org.sa.APP;

import org.sa.a_config.FilePath;
import org.sa.a_config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.service.ConceptsLoader;
import org.sa.util.FileUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CheckConceptsAttemptsAPP {

  record AttemptRecord(String key, String definition, int evaluation, LocalDateTime timestamp) {}

  public static void main(String[] args) {
    //prep objects
    CheckConceptsAttemptsAPP app = new CheckConceptsAttemptsAPP();
    ConceptsLoader loader = new ConceptsLoader();

    //prep data structure
    List<String> attemptLines = FileUtil.listLines(FilePath.ATTEMPTED_ANSWERS.toFile());
    List<AttemptRecord> attempts = attemptLines.stream().map(CheckConceptsAttemptsAPP::parseLineToAttempt).filter(Objects::nonNull).filter(attempt -> loader.key_concept.containsKey(attempt.key)).collect(Collectors.toList());
    System.out.println("\nNumber of user answer attempts so far (for non outdated keys): " + attempts.size());
    Map<String, List<AttemptRecord>> key_attempts = attempts.stream().collect(Collectors.groupingBy(AttemptRecord::key));

    //PRINTOUT

    //what were the grades
    app.printKeyGradesThatAreAllBelowSomeGrade_veryConcise(key_attempts, 9);

    //what were the answers?
    //app.printGroupedAttempts_veryExplicit(key_attempts, loader);


  }

  private static AttemptRecord parseLineToAttempt(String line) {
    String[] parts = line.split(",", 4);
    if (parts.length < 4) return null;
    String concept = parts[0];
    String definition = parts[1];
    int evaluation = Integer.parseInt(parts[2]);
    LocalDateTime timestamp = LocalDateTime.parse(parts[3]);
    return new AttemptRecord(concept, definition, evaluation, timestamp);
  }

  public void printGroupedAttempts_veryExplicit(Map<String, List<AttemptRecord>> keyAttempts, ConceptsLoader loader) {
    keyAttempts.forEach((key, attempts) -> {
      int score = loader.key_concept.get(key).score;
      SimpleColorPrint.red(key + ": ");
      SimpleColorPrint.normal(Props.TAB + Colors.LIGHT_GRAY + "score: " + score + Colors.RESET);
      attempts.forEach(a -> {
        SimpleColorPrint.blueInLine(Props.TAB.repeat(2) + String.valueOf(a.evaluation()));
        SimpleColorPrint.normalInLine(" | ");
        SimpleColorPrint.yellowInLine(a.definition());
        SimpleColorPrint.normalInLine(" | ");
        SimpleColorPrint.normal(a.timestamp().toString());
      });
      System.out.println();
    });
  }

  public void printKeyGradesThatAreAllBelowSomeGrade_veryConcise(Map<String, List<AttemptRecord>> keyAttempts, int belowThisGrade) {
    System.out.println("\n");
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
