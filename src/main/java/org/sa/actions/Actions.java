package org.sa.actions;

import org.sa.AiClient;
import org.sa.concepts.Concepts;
import org.sa.config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class Actions {

  private Concepts concepts;
  private AiClient evaluatorAi = new AiClient().setModelGpt4oMini();
  private AiClient answersAi = new AiClient().setModelGpt4oMini();
  private static final Path ATTEMPTED_ANSWERS_FILEPATH = Paths.get("src/main/java/org/sa/storage/attempted_answers.csv");

  public Actions(Concepts concepts) throws IOException {
    this.concepts = concepts;
  }

  public ConceptDTO pickConceptWithLowestScore() {
    SimpleColorPrint.blue("Picking concept with lowest score...\n");

    concepts.refreshNotTodayMap();
    Set<String> skippableKeys = concepts.notTodayKey_time.keySet();

    for (List<String> keys : concepts.score_keyList.values()) {
      List<String> eligibleKeys = new ArrayList<>();

      for (String key : keys)
        if (!skippableKeys.contains(key))
          eligibleKeys.add(key);

      if (eligibleKeys.isEmpty()) continue;
      String randomKey = eligibleKeys.get(new Random().nextInt(eligibleKeys.size()));
      return concepts.key_concept.get(randomKey);
    }

    throw new NoSuchElementException("No eligible concept available");
  }

  public ConceptDTO pickConceptWithFragmentInKey(String fragment) {
    if (fragment == null || fragment.isEmpty()) {
      SimpleColorPrint.red("Fragment is empty");
      return pickConceptWithLowestScore();
    }

    SimpleColorPrint.blueInLine("Picking key containing fragment: ");
    SimpleColorPrint.red(fragment + "\n");

    return concepts.key_concept.entrySet()
      .stream()
      .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
      .findFirst()
      .map(entry -> {
        SimpleColorPrint.redInLine("Picked key: ");
        SimpleColorPrint.blue(entry.getKey());
        return concepts.key_concept.get(entry.getKey());
      })
      .orElseGet(() -> {
        SimpleColorPrint.blueInLine("Not found any concept containing fragment: ");
        SimpleColorPrint.red(fragment);
        return pickConceptWithLowestScore();
      });
  }

  public ConceptDTO pickNthKeyDefinition(String fragment, int nthInstance) {
    List<Entry<String, ConceptDTO>> matchingKey_Concept = concepts.key_concept
        .entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (matchingKey_Concept.size() == 0) {
      SimpleColorPrint.redInLine("Not found any concept containing fragment: ");
      SimpleColorPrint.blue(fragment + "\n");
      return pickConceptWithLowestScore();
    }

    if (matchingKey_Concept.size() - 1 < nthInstance) {
      SimpleColorPrint.redInLine("There are only ");
      SimpleColorPrint.blueInLine(String.valueOf(matchingKey_Concept.size()));
      SimpleColorPrint.redInLine(" matches for the fragment ");
      SimpleColorPrint.blueInLine(fragment);
      SimpleColorPrint.redInLine(". The entered 'nth' value is too high: ");
      SimpleColorPrint.blue(String.valueOf(nthInstance) + "\n");
      Info.printKeyEntryList_indexed_fragmentHighlighted(fragment, matchingKey_Concept);
      return pickConceptWithLowestScore();
    }

    Info.printKeyEntryList_indexed_fragmentHighlighted(fragment, matchingKey_Concept);
    return concepts.key_concept.get(matchingKey_Concept.get(nthInstance).getKey());
  }

  public void askAi(String input) {
    SimpleColorPrint.yellow(answersAi.getAnswer(input) + "\n");
  }

  public ConceptDTO pickNthConceptWithFragmentInKey(String input) {
    //pick nth <fragment nth> - pick nth key containing fragment;
    String fragmentToSearchForAndNumber = input.substring("pick nth".length()); //should be "<fragment nth>"
    final String ENDS_WITH_SPACE_AND_DIGITS_PATTERN = "^(.*) (\\d+)$";

    if (!fragmentToSearchForAndNumber.matches(ENDS_WITH_SPACE_AND_DIGITS_PATTERN)) {
      SimpleColorPrint.blueInLine("pick nth <fragment nth>, ");
      SimpleColorPrint.redInLine(fragmentToSearchForAndNumber);
      SimpleColorPrint.normal(" - did not end with space and number.");
      SimpleColorPrint.red("'pick nth <fragment nth>' was aborted due to not matching ENDS_WITH_SPACE_AND_DIGITS_PATTERN = \"(.*) (\\d+)$\"");
      return pickConceptWithFragmentInKey(input.substring("pick nth".length()).trim());
    } else {
      String fragment = fragmentToSearchForAndNumber.replaceAll(ENDS_WITH_SPACE_AND_DIGITS_PATTERN, "$1").trim();
      int nth = Integer.parseInt(fragmentToSearchForAndNumber.replaceAll(ENDS_WITH_SPACE_AND_DIGITS_PATTERN, "$2"));
      SimpleColorPrint.blueInLine("Searching for fragment: ");
      SimpleColorPrint.redInLine(fragment);
      SimpleColorPrint.blueInLine(" nth: ");
      SimpleColorPrint.red(String.valueOf(nth));
      return pickNthKeyDefinition(fragment, nth);
    }
  }

  public void save() throws IOException {
    BufferedWriter writer = Files.newBufferedWriter(concepts.SCORE_PATH);

    for (Entry<String, Integer> e : concepts.key_score.entrySet())
      writer.write(e.getKey().replaceAll("([ \\t\\n\\r\\f=:])", "\\\\$1") + "=" + e.getValue() + "\n");

    writer.flush();
    writer.close();
  }

  public ConceptDTO answerIDontKnow(ConceptDTO concept) throws IOException {
    ConceptDTO c = new ConceptDTO(concept.key, concept.definition);
    incrementScore(c.key, -1);
    SimpleColorPrint.blue("Concept has received a score of -1: ");
    SimpleColorPrint.red(Props.TAB + c.key + ": " + c.definition + "\n");
    concepts.dontLearnThisToday(c.key);
    return pickConceptWithLowestScore();
  }

  public void incrementScore(String key, int increment) {
    Integer initialScore = concepts.key_score.get(key);
    if (initialScore == null) initialScore = 0;
    concepts.key_score.merge(key, increment, Integer::sum);
    int finalScore = concepts.key_score.get(key);
    if (finalScore == 0) concepts.key_score.remove(key);

    List<String> initialList = concepts.score_keyList.get(initialScore);
    if (initialList.size() == 1) concepts.score_keyList.remove(initialScore);
    else initialList.remove(key);

    concepts.score_keyList.computeIfAbsent(finalScore, k -> new ArrayList<>()).add(key);
  }

  private String extractEvaluationString(String s) {
    return Pattern.compile("\\b([0-9]|10)/10\\b").matcher(s).results().map(matchResult -> matchResult.group()).findFirst().orElse("");
  }

  public ConceptDTO evaluateUserExplanationWithAI(ConceptDTO concept, String userInputDefinitionAttempt, String instructionToEvaluateUserInput) throws IOException {
    ConceptDTO c = new ConceptDTO(concept.key, concept.definition);


    //AI evaluation
    String answer = evaluatorAi.getAnswer(instructionToEvaluateUserInput) + "\n";
    String evaluationString = extractEvaluationString(answer);
    int evaluation = Integer.parseInt(evaluationString.split("/")[0]);

    if ("".equals(evaluationString)) {
      SimpleColorPrint.red("The AI has not provided the evaluation. Try again. AI answer: \n" + answer);
      return c;
    }

    Info.printStringWithFragmentHighlighted(evaluationString, answer, Colors.YELLOW, Colors.RED);

    //memorize answer
    String userAttemptedDefinition = userInputDefinitionAttempt.replace(",", ";");
    String recordLine = String.join(",", c.key, userAttemptedDefinition, String.valueOf(evaluation), LocalDateTime.now().toString()) + "\n";
    try (BufferedWriter writer = Files.newBufferedWriter(ATTEMPTED_ANSWERS_FILEPATH, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      writer.write(recordLine);
    }

    //score, put to "not_today", print default answer
    incrementScore(c.key, evaluation < 7 ? -1 : evaluation <= 8 ? 1 : evaluation == 9 ? 2 : 4);
    concepts.dontLearnThisToday(c.key);
    SimpleColorPrint.blueInLine("The default definition: ");
    SimpleColorPrint.normal(c.definition + "\n");

    return pickConceptWithLowestScore();
  }

  public ConceptDTO addKeywordToNotToday(ConceptDTO concept) throws IOException {
    ConceptDTO c = new ConceptDTO(concept.key, concept.definition);
    concepts.dontLearnThisToday(c.key);
    return pickConceptWithLowestScore();
  }
}

