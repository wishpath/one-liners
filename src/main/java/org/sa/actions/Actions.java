package org.sa.actions;

import org.sa.AiClient;
import org.sa.concepts.Concepts;
import org.sa.config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.service.AdditionalInstructionsToEvaluate;

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

  private AdditionalInstructionsToEvaluate instruction;
  private Concepts concepts;
  private AiClient ai;
  private static final Path ATTEMPTED_ANSWERS_FILEPATH = Paths.get("src/main/java/org/sa/storage/attempted_answers.csv");

  public Actions(Concepts concepts, AiClient ai, AdditionalInstructionsToEvaluate instruction) throws IOException {
    this.concepts = concepts;
    this.ai = ai;
    this.instruction = instruction;
  }

  public Entry<String, String> pickConceptWithLowestScore() {
    SimpleColorPrint.blue("Picking concept with lowest score...\n");

    concepts.refreshNotTodayMap();
    Set<String> skippableKeys = concepts.notTodayKeys.keySet();

    for (List<String> keys : concepts.scoreToKeys.values()) {
      List<String> eligibleKeys = new ArrayList<>();

      for (String key : keys)
        if (!skippableKeys.contains(key))
          eligibleKeys.add(key);

      if (eligibleKeys.isEmpty()) continue;
      String randomKey = eligibleKeys.get(new Random().nextInt(eligibleKeys.size()));
      return Map.entry(randomKey, concepts.keyDefinition.get(randomKey));
    }

    throw new NoSuchElementException("No eligible concept available");
  }

  public Entry<String, String> pickConceptWithFragmentInKey(String fragment) {
    if (fragment == null || fragment.isEmpty()) {
      SimpleColorPrint.red("Fragment is empty");
      return pickConceptWithLowestScore();
    }

    SimpleColorPrint.blueInLine("Picking key containing fragment: ");
    SimpleColorPrint.red(fragment + "\n");

    return concepts.keyDefinition.entrySet()
      .stream()
      .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
      .findFirst()
      .map(entry -> {
        SimpleColorPrint.redInLine("Picked key: ");
        SimpleColorPrint.blue(entry.getKey());
        return entry;
      })
      .orElseGet(() -> {
        SimpleColorPrint.blueInLine("Not found any concept containing fragment: ");
        SimpleColorPrint.red(fragment);
        return pickConceptWithLowestScore();
      });
  }

  public Entry<String, String> pickNthKeyDefinition(String fragment, int nthInstance) {
    List<Entry<String, String>> matchingKey_Definition = concepts.keyDefinition
        .entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (matchingKey_Definition.size() == 0) {
      SimpleColorPrint.redInLine("Not found any concept containing fragment: ");
      SimpleColorPrint.blue(fragment + "\n");
      return pickConceptWithLowestScore();
    }

    if (matchingKey_Definition.size() - 1 < nthInstance) {
      SimpleColorPrint.redInLine("There are only ");
      SimpleColorPrint.blueInLine(String.valueOf(matchingKey_Definition.size()));
      SimpleColorPrint.redInLine(" matches for the fragment ");
      SimpleColorPrint.blueInLine(fragment);
      SimpleColorPrint.redInLine(". The entered 'nth' value is too high: ");
      SimpleColorPrint.blue(String.valueOf(nthInstance) + "\n");
      Info.printKeyEntryList_indexed_fragmentHighlighted(fragment, matchingKey_Definition);
      return pickConceptWithLowestScore();
    }

    Info.printKeyEntryList_indexed_fragmentHighlighted(fragment, matchingKey_Definition);
    return matchingKey_Definition.get(nthInstance);
  }

  public void askAi(String input) {
    SimpleColorPrint.yellow(ai.getAnswer(input) + "\n");
  }

  public Entry<String, String> pickNthConceptWithFragmentInKey(String input) {
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

    for (Entry<String, Integer> e : concepts.keyScore.entrySet())
      writer.write(e.getKey().replaceAll("([ \\t\\n\\r\\f=:])", "\\\\$1") + "=" + e.getValue() + "\n");

    writer.flush();
    writer.close();
  }

  public Entry<String, String> answerIDontKnow(Entry<String, String> concept) throws IOException {
    incrementScore(concept.getKey(), -1);
    SimpleColorPrint.blue("Concept has received a score of -1: ");
    SimpleColorPrint.red(Props.TAB + concept.getKey() + ": " + concept.getValue() + "\n");
    concepts.dontLearnThisToday(concept.getKey());
    return pickConceptWithLowestScore();
  }

  public void incrementScore(String key, int increment) {
    Integer initialScore = concepts.keyScore.get(key);
    if (initialScore == null) initialScore = 0;
    concepts.keyScore.merge(key, increment, Integer::sum);
    int finalScore = concepts.keyScore.get(key);
    if (finalScore == 0) concepts.keyScore.remove(key);

    List<String> initialList = concepts.scoreToKeys.get(initialScore);
    if (initialList.size() == 1) concepts.scoreToKeys.remove(initialScore);
    else initialList.remove(key);

    concepts.scoreToKeys.computeIfAbsent(finalScore, k -> new ArrayList<>()).add(key);
  }

  private String extractEvaluationString(String s) {
    return Pattern.compile("\\b([0-9]|10)/10\\b")
        .matcher(s)
        .results()
        .map(matchResult -> matchResult.group())
        .findFirst()
        .orElse("");
  }

  public Entry<String, String> evaluateUserExplanationWithAI(Entry<String, String> concept, String userInputDefinitionAttempt, String instructionToEvaluateUserInput) throws IOException {
    //just testing
    if (instruction.key_instructions.containsKey(concept.getKey())) System.out.println(concept.getKey() + " CONTAINS EXTRA INSTRUCTION");
    instructionToEvaluateUserInput = instructionToEvaluateUserInput.replace("here_will_be_userInputDefinitionAttempt", userInputDefinitionAttempt);
    System.out.println(instructionToEvaluateUserInput);
    //AI evaluation
    String answer = ai.getAnswer(instructionToEvaluateUserInput) + "\n";
    String evaluationString = extractEvaluationString(answer);
    int evaluation = Integer.parseInt(evaluationString.split("/")[0]);

    if ("".equals(evaluationString)) {
      SimpleColorPrint.red("The AI has not provided the evaluation. Try again. AI answer: \n" + answer);
      return concept;
    }

    Info.printStringWithFragmentHighlighted(evaluationString, answer, Colors.YELLOW, Colors.RED);

    //memorize answer
    String definition = userInputDefinitionAttempt.replace(",", ";");
    String recordLine = String.join(",", concept.getKey(), definition, String.valueOf(evaluation), LocalDateTime.now().toString()) + "\n";
    try (BufferedWriter writer = Files.newBufferedWriter(ATTEMPTED_ANSWERS_FILEPATH, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      writer.write(recordLine);
    }

    //score, put to "not_today", print default answer
    incrementScore(concept.getKey(), evaluation < 7 ? -1 : evaluation <= 8 ? 1 : evaluation == 9 ? 2 : 4);
    concepts.dontLearnThisToday(concept.getKey());
    SimpleColorPrint.blueInLine("The default definition: ");
    SimpleColorPrint.normal(concept.getValue() + "\n");

    return pickConceptWithLowestScore();
  }

  public Entry<String, String> addKeywordToNotToday(Entry<String, String> concept) throws IOException {
    concepts.dontLearnThisToday(concept.getKey());
    return pickConceptWithLowestScore();
  }
}

