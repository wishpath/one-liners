package org.sa;

import org.sa.concepts.Concepts;
import org.sa.console.SimpleColorPrint;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class Actions {

  private Concepts concepts = new Concepts();
  private AiClient ai = new AiClient();

  public Actions() throws IOException {
  }

  public Map.Entry<String, String> pickConceptWithLowestScore() {
    SimpleColorPrint.blue("Picking concept with lowest score...");
    String key = concepts.scoreKeyList.firstEntry().getValue().getFirst();
    String value = concepts.keyDefinition.get(key);
    return Map.entry(key, value);
  }

  public Map.Entry<String, String> pickConceptWithFragmentInKey(String fragment) {
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


  public Map.Entry<String, String> pickNthKeyDefinition(String fragment, int nthInstance) {
    return concepts.keyDefinition.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .skip(nthInstance + 1)
        .findFirst()
        .orElseGet(() -> {
          SimpleColorPrint.blueInLine("Not found ");
          SimpleColorPrint.redInLine(String.valueOf(nthInstance));
          SimpleColorPrint.blueInLine("-th concept containing fragment: ");
          SimpleColorPrint.red(fragment);
          return pickConceptWithLowestScore();
        });
  }

  public void printAllConceptsContainingFragmentInKey(String fragment) {
    List<Map.Entry<String, String>> found = concepts.keyDefinition.entrySet().stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (found.isEmpty()) {
      SimpleColorPrint.redInLine("No keys found containing fragment: ");
      SimpleColorPrint.red(fragment);
    }
    else {
      SimpleColorPrint.blueInLine("Defining all keys containing fragment: ");
      SimpleColorPrint.red(fragment);
      found.forEach(entry -> printConceptWithFragment(entry, fragment));
    }
    System.out.println();
  }

  public void printAllConceptsContainingFragmentInKeyValue(String fragment) {
    List<Map.Entry<String, String>> found = concepts.keyDefinition.entrySet().stream()
        .filter(entry -> (entry.getKey() + " " + entry.getValue()).toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (found.isEmpty()) {
      SimpleColorPrint.redInLine("No key-values found containing fragment: ");
      SimpleColorPrint.red(fragment);
    }
    else {
      SimpleColorPrint.blueInLine("Defining all key-values containing fragment: ");
      SimpleColorPrint.red(fragment);
      found.forEach(entry -> printConceptWithFragment(entry, fragment));
    }
    System.out.println();
  }

  private static void printConceptWithFragment2(Map.Entry<String, String> entry, String fragment) {
    System.out.println("\n" + entry.getKey() + ":");
    System.out.println(entry.getValue());
  }

  private static void printConceptWithFragment(Map.Entry<String, String> entry, String fragment) {
    String concept = "\n" + entry.getKey() + ":\n" + entry.getValue() + "\n";
    String lowerConcept = concept.toLowerCase();
    String lowerFragment = fragment.toLowerCase();
    int start = 0;

    while (start < concept.length()) {
      int index = lowerConcept.indexOf(lowerFragment, start);
      if (index == -1) {
        SimpleColorPrint.normal(concept.substring(start));
        break;
      }
      SimpleColorPrint.normalInLine(concept.substring(start, index));
      SimpleColorPrint.redInLine(concept.substring(index, index + fragment.length()));
      start = index + fragment.length();
    }
  }

  public void printAllKeys() {
    SimpleColorPrint.blue("Listing all the keys:");
    concepts.keyDefinition.entrySet()
        .stream()
        .forEach(entry -> {
          System.out.print(entry.getKey() + ", ");
        });
    System.out.println();
  }

  public void askAi(String input) {
    SimpleColorPrint.yellow(ai.getAnswer(input));
  }

  public Map.Entry<String, String> evaluateUserExplanationWithAI(Map.Entry<String, String> concept, String input) {
    String questionB =
        "Is this a good key and definition: " + concept.getKey() + " = " + input + ". " +
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
    SimpleColorPrint.yellow(answer);

    if (parseEvaluation(answer) >= 7) {
      concepts.incrementScore(concept.getKey(), 1);
      return pickConceptWithLowestScore();
    }
    concepts.incrementScore(concept.getKey(), -1);
    return concept;
  }

  private int parseEvaluation(String s) {
    try {
      return Pattern.compile("\\b([0-9]|10)/10\\b")
          .matcher(s)
          .results()
          .map(match -> Integer.parseInt(match.group(1)))
          .findFirst()
          .orElse(-1);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  public Map.Entry<String, String> pickNthConceptWithFragmentInKey(String input) {
    //pick nth <fragment nth> - pick nth key containing fragment;
    String fragmentToSearchForAndNumber = input.substring("pick nth ".length()); //should be "<fragment nth>"
    String endsWithSpaceDigitsPattern = "^(.*) (\\d+)$";

    if (!fragmentToSearchForAndNumber.matches(endsWithSpaceDigitsPattern)) {
      SimpleColorPrint.blueInLine("pick nth <fragment nth>, ");
      SimpleColorPrint.redInLine(fragmentToSearchForAndNumber);
      SimpleColorPrint.normal(" - did not end with space and number.");
      SimpleColorPrint.red("pick nth <fragment nth> was aborted");
      return pickConceptWithFragmentInKey(input.substring("pick nth ".length()));
    }
    else {
      String fragment = fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$1");
      int nth = Integer.parseInt(fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$2"));
      SimpleColorPrint.blueInLine("Searching for fragment: ");
      SimpleColorPrint.redInLine(fragment );
      SimpleColorPrint.blueInLine(" nth: ");
      SimpleColorPrint.red(String.valueOf(nth));
      return pickNthKeyDefinition(fragment, nth);
    }
  }

  public void save() throws IOException {
    Properties props = new Properties();

    // Convert the <String, Integer> map to <String, String> and putAll
    concepts.keyScore.forEach((key, value) -> props.put(key, String.valueOf(value)));

    //write
    props.store(Files.newBufferedWriter(concepts.SCORE_PATH), "Concept key to score (1 for correct, -1 for wrong)");
    System.out.println(Files.lines(concepts.SCORE_PATH).filter(line -> line.contains("=")).count() + " lines in the score file");
  }

  public void end() throws IOException {
    save();
  }

  public Map.Entry<String, String> answerIDontKnow(Map.Entry<String, String> concept) {
    concepts.incrementScore(concept.getKey(), -1);
    SimpleColorPrint.blue("Concept has received a score of -1: ");
    SimpleColorPrint.red(concept.getKey() + " - " + concept.getValue());
    return pickConceptWithLowestScore();
  }
}
