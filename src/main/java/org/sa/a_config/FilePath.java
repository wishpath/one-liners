package org.sa.a_config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilePath {
  //general
  private static final String STORAGE = "src/main/java/org/sa/storage/";

  //source
  public static final Path TOPICS_PUBLIC = Paths.get(STORAGE + "concepts/topics");
  public static final Path CONCEPT_FILES = Paths.get(STORAGE + "concepts/");

  //saves
  public static final Path SCORE_PATH = Paths.get(STORAGE + "saves/score.properties");
  public static final Path NOT_TODAY_FILEPATH = Paths.get(STORAGE + "saves/not_today.csv");
  public static final Path ATTEMPTED_ANSWERS = Paths.get(STORAGE + "saves/attempted_answers.csv");

  //wiki
  public static final Path WIKI = Paths.get(STORAGE + "wiki/wiki_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".txt");
}