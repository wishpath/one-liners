package org.sa.actions;

import org.sa.AiClient;
import org.sa.concepts.Concepts;
import org.sa.config.Props;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.service.AdditionalInstructionsToEvaluate;
import org.sa.service.InstructionTextForAi;
import org.sa.service.NotTodayService;

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

  private final NotTodayService notTodayService;
  private Concepts concepts;
  private AiClient evaluatorAi = new AiClient().setModelGpt4oMini();
  private AiClient answersAi = new AiClient().setModelGpt4oMini();
  private AdditionalInstructionsToEvaluate instructionsToEvaluate;
  private static final Path ATTEMPTED_ANSWERS_FILEPATH = Paths.get("src/main/java/org/sa/storage/attempted_answers.csv");

  public Actions(Concepts concepts, NotTodayService notTodayService) throws IOException {
    this.notTodayService = notTodayService;
    this.concepts = concepts;
    this.instructionsToEvaluate = new AdditionalInstructionsToEvaluate(concepts);
  }

  public ConceptDTO pickConceptWithLowestScore() {
    SimpleColorPrint.blue("Picking concept with lowest score...\n");

    notTodayService.refreshNotTodayMap();
    Set<String> skippableKeys = notTodayService.notTodayKey_time.keySet();

    for (Set<String> keys : concepts.score_keySet.values()) {
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

  public void saveScores_OverwriteFile() throws IOException {
    BufferedWriter writer = Files.newBufferedWriter(org.sa.config.Paths.SCORE_PATH);

    for (Entry<String, Integer> e : concepts.key_score.entrySet()) {
      // escapes these characters: space, tab, newline, carriage return, formfeed, '=', ':'
      String escapedKeyForPropertiesFileFormat = e.getKey().replaceAll("([ \\t\\n\\r\\f=:])", "\\\\$1");
      writer.write( escapedKeyForPropertiesFileFormat + "=" + e.getValue() + "\n");
    }

    writer.flush();
    writer.close();
  }

  public ConceptDTO answerIDontKnow(ConceptDTO c){
    incrementScore(c, -1);
    SimpleColorPrint.blue("Concept has received a score of -1: ");
    SimpleColorPrint.red(Props.TAB + c.key + ": " + c.definition + "\n");
    notTodayService.dontLearnThisToday(c.key);
    return pickConceptWithLowestScore();
  }

  public void incrementScore(ConceptDTO conceptDTO, int increment) {
    //remove key from score map (from old score)
    Set<String> initialSet = concepts.score_keySet.get(conceptDTO.score);
    if (initialSet.size() == 1) concepts.score_keySet.remove(conceptDTO.score);
    else initialSet.remove(conceptDTO.key);

    //increase score in the datastructures:
    concepts.key_score.merge(conceptDTO.key, increment, Integer::sum); //TODO to be removed later
    conceptDTO.score += increment;

    //add key back to score map (to new score)
    concepts.score_keySet.computeIfAbsent(conceptDTO.score, k -> new HashSet<>()).add(conceptDTO.key);
  }

  private String extractEvaluationString(String s) {
    return Pattern.compile("\\b([0-9]|10)/10\\b").matcher(s).results().map(matchResult -> matchResult.group()).findFirst().orElse("");
  }

  public ConceptDTO evaluateUserExplanationWithAI(ConceptDTO concept, String userInputDefinitionAttempt) throws IOException {

    String instructionToEvaluateUserInput = InstructionTextForAi.getInstructionToEvaluateUserInput(concept, instructionsToEvaluate, userInputDefinitionAttempt);

    //AI evaluation
    String answer = evaluatorAi.getAnswer(instructionToEvaluateUserInput) + "\n";

    //get evaluation out of 10
    String evaluationOutOfTenString = extractEvaluationString(answer);
    int evaluationOutOfTen = Integer.parseInt(evaluationOutOfTenString.split("/")[0]);
    if ("".equals(evaluationOutOfTenString)) {
      SimpleColorPrint.red("The AI has not provided the evaluation. Try again. AI answer: \n" + answer);
      return concept;
    }

    Info.printStringWithFragmentHighlighted(evaluationOutOfTenString, answer, Colors.YELLOW, Colors.RED);

    //memorize answer
    String userAttemptedDefinition = userInputDefinitionAttempt.replace(",", ";");
    String recordLine = String.join(",", concept.key, userAttemptedDefinition, String.valueOf(evaluationOutOfTen), LocalDateTime.now().toString()) + "\n";
    try (BufferedWriter writer = Files.newBufferedWriter(ATTEMPTED_ANSWERS_FILEPATH, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      writer.write(recordLine);
    }

    //score, put to "not_today", print default answer
    incrementScore(concept, evaluationOutOfTen < 7 ? -1 : evaluationOutOfTen <= 8 ? 1 : evaluationOutOfTen == 9 ? 2 : 4);
    notTodayService.dontLearnThisToday(concept.key);
    SimpleColorPrint.blueInLine("The default definition: ");
    SimpleColorPrint.normal(concept.definition + "\n");

    return pickConceptWithLowestScore();
  }

  public ConceptDTO addKeywordToNotToday(ConceptDTO concept) throws IOException {
    ConceptDTO c = new ConceptDTO(concept.key, concept.definition);
    notTodayService.dontLearnThisToday(c.key);
    return pickConceptWithLowestScore();
  }
}

