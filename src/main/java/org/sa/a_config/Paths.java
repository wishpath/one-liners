package org.sa.a_config;

import java.nio.file.Path;

public class Paths {
  public static final Path SCORE_PATH = java.nio.file.Paths.get("src/main/java/org/sa/storage/score.properties");
  public static final Path NOT_TODAY_FILEPATH = java.nio.file.Paths.get("src/main/java/org/sa/storage/not_today.csv");
  public static final Path TOPICS_SWED = java.nio.file.Paths.get("src/main/java/org/sa/storage/concepts/topics-swed");
  public static final Path ATTEMPTED_ANSWERS_FILEPATH = java.nio.file.Paths.get("src/main/java/org/sa/storage/attempted_answers.csv");
}
