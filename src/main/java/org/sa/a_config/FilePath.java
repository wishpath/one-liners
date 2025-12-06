package org.sa.a_config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePath {
  public static final Path SCORE_PATH = Paths.get("src/main/java/org/sa/storage/score.properties");
  public static final Path NOT_TODAY_FILEPATH = Paths.get("src/main/java/org/sa/storage/not_today.csv");
  public static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/storage/concepts/topics-swed");
  public static final Path TOPICS_PUBLIC = Paths.get("src/main/java/org/sa/storage/concepts/topics");
  public static final Path ATTEMPTED_ANSWERS = Paths.get("src/main/java/org/sa/storage/attempted_answers.csv");
  public static final Path INSTRUCTIONS_TO_EVALUATE = Paths.get("src/main/java/org/sa/storage/instructions_to_evaluate.csv");
  public static final Path WIKI_SWED_OUTPUT_FILE = Paths.get("src/main/java/org/sa/storage/concepts/wiki_swed.txt");
  public static final Path WIKI_PUBLIC_OUTPUT_FILE = Paths.get("src/main/java/org/sa/storage/concepts/wiki_public.txt");
}
