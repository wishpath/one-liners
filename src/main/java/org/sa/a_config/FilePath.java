package org.sa.a_config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePath {
  //general
  private static final String STORAGE = "src/main/java/org/sa/storage/";

  //source
  public static final Path TOPICS_SWED = Paths.get(STORAGE + "concepts/topics-swed");
  public static final Path TOPICS_PUBLIC = Paths.get(STORAGE + "concepts/topics");

  //saves
  public static final Path SCORE_PATH = Paths.get(STORAGE + "saves/score.properties");
  public static final Path NOT_TODAY_FILEPATH = Paths.get(STORAGE + "saves/not_today.csv");
  public static final Path ATTEMPTED_ANSWERS = Paths.get(STORAGE + "saves/attempted_answers.csv");
  public static final Path INSTRUCTIONS_TO_EVALUATE = Paths.get(STORAGE + "saves/instructions_to_evaluate.csv");
  public static final Path INSTRUCTIONS_FOR_USER = Paths.get(STORAGE + "saves/instructions_for_user.csv");

  //wiki
  public static final Path WIKI_SWED_OUTPUT_FILE = Paths.get(STORAGE + "wiki/wiki_swed.txt");
  public static final Path WIKI_PUBLIC_OUTPUT_FILE = Paths.get(STORAGE + "wiki/wiki_public.txt");
}