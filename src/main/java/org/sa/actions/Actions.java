package org.sa.actions;

import org.sa.AiClient;
import org.sa.concepts.Concepts;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;

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
  private AiClient ai;
  private static final Path ATTEMPTED_ANSWERS = Paths.get("src/main/java/org/sa/attempted_answers.csv");

  public Actions(Concepts concepts, AiClient ai) throws IOException {
    this.concepts = concepts;
    this.ai = ai;
  }

  public Entry<String, String> pickConceptWithLowestScore() {
    SimpleColorPrint.blue("Picking concept with lowest score...");

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
    SimpleColorPrint.blueInLine("Picking key containing fragment ");
    SimpleColorPrint.red(fragment);

    return concepts.keyDefinition.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .findFirst()
        .orElseGet(() -> {
          SimpleColorPrint.blueInLine("Not found any concept containing fragment: ");
          SimpleColorPrint.red(fragment);
          return pickConceptWithLowestScore();
        });
  }


  public Entry<String, String> pickNthKeyDefinition(String fragment, int nthInstance) {
    return concepts.keyDefinition.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .skip(nthInstance)
        .findFirst()
        .orElseGet(() -> {
          SimpleColorPrint.blueInLine("Not found ");
          SimpleColorPrint.redInLine(String.valueOf(nthInstance));
          SimpleColorPrint.blueInLine("-th concept containing fragment: ");
          SimpleColorPrint.red(fragment);
          return pickConceptWithLowestScore();
        });
  }

  public void askAi(String input) {
    SimpleColorPrint.yellow(ai.getAnswer(input));
  }

  public Entry<String, String> pickNthConceptWithFragmentInKey(String input) {
    //pick nth <fragment nth> - pick nth key containing fragment;
    String fragmentToSearchForAndNumber = input.substring("pick nth ".length()); //should be "<fragment nth>"
    String endsWithSpaceDigitsPattern = "^(.*) (\\d+)$";

    if (!fragmentToSearchForAndNumber.matches(endsWithSpaceDigitsPattern)) {
      SimpleColorPrint.blueInLine("pick nth <fragment nth>, ");
      SimpleColorPrint.redInLine(fragmentToSearchForAndNumber);
      SimpleColorPrint.normal(" - did not end with space and number.");
      SimpleColorPrint.red("pick nth <fragment nth> was aborted");
      return pickConceptWithFragmentInKey(input.substring("pick nth ".length()));
    } else {
      String fragment = fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$1");
      int nth = Integer.parseInt(fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$2"));
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
    SimpleColorPrint.red(concept.getKey() + " - " + concept.getValue());
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

  public Entry<String, String> evaluateUserExplanationWithAI(Entry<String, String> concept, String userInputDefinitionAttempt) throws IOException {
    //AI evaluation
    String questionB =
        "Is this a good key and definition: " + concept.getKey() + " = " + userInputDefinitionAttempt + ". " +
            "\n 1 - Evaluate the answer by asking: 'Does this capture the essence?' (aim to be positive)." +
            "\n If some details are missing but it captures the essence, rate 10/10." +
            "\n If the definition matches this one, rate 10/10: " + concept.getValue() +
            "\n If the essence is ALMOST there, rate 9/10." +
            "\n If the essence is somewhat touched, rate 8/10." +
            "\n Think of an answer in up to 10 words - if you can't come up with a better one, rate 10/10." +
            "\n If the answer is completely off, rate 0/10." +
            "\n If the answer is somewhat acceptable, rate 7/10." +
            "\n If the key is an acronym, the definition must include exact words for each letter (e.g., 'Intelligence Quotient' for IQ); other correct answers (like 'a measure of smartness') are not acceptable, and the maximum score is 7/10." +
            "\n Step 2 - If the evaluation is less than 7/10, provide the correct answer (if 7/10 to 10/10, skip this step)." +
            "\n Your entire answer should be up to 300 characters.";
    String answer = ai.getAnswer(questionB);
    String evaluationString = extractEvaluationString(answer);
    int evaluation = Integer.parseInt(evaluationString.split("/")[0]);

    if ("".equals(evaluationString)) {
      SimpleColorPrint.red("The AI has not provided the evaluation. Try again. AI answer: \n" + answer);
      return concept;
    }

    Info.printStringWithFragmentHighlighted(evaluationString, answer, Colors.YELLOW, Colors.RED);

    //memorize answer
    String definition = userInputDefinitionAttempt.replace(";", ",");
    String recordLine = String.join(";", concept.getKey(), definition, String.valueOf(evaluation), LocalDateTime.now().toString()) + "\n";
    try (BufferedWriter writer = Files.newBufferedWriter(ATTEMPTED_ANSWERS, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      writer.write(recordLine);
    }

    //depending on evaluation return concept entry
    incrementScore(concept.getKey(), evaluation < 7 ? -1 : evaluation <= 8 ? 1 : evaluation == 9 ? 2 : 4);
    concepts.dontLearnThisToday(concept.getKey());

    SimpleColorPrint.blueInLine("The default definition: ");
    SimpleColorPrint.normal(concept.getValue() + "\n");
    return pickConceptWithLowestScore();
  }
}

