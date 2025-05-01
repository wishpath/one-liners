package org.sa;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class ScannerApp {

  private static final String EDIT = "edit: ";
  private Scanner scanner = new Scanner(System.in);
  private Functions functions = new Functions();
  private AiClient ai = new AiClient();
  private Map.Entry<String, String> keyDefinition = functions.pickRandomKeyDefinition();
  private String key = keyDefinition.getKey();
  private String definition = keyDefinition.getValue();


  public ScannerApp() throws IOException {
    while (true) {
      System.out.println("Explain this: " + key);
      String input = scanner.nextLine().trim();

      if (input.isEmpty()) continue;
      else if ("menu".equals(input)) printMenu();
      else if ("skip".equals(input) || "s".equals(input)) reloadKey();
      else if ("all keys".equals(input)) functions.printAllKeys();
      else if (input.startsWith("define all all ")) printAllKeyValuesContainingFragment(input.substring("define all all ".length()));
      else if (input.startsWith("define all ")) printAllKeysContainingFragment(input.substring("define all ".length()));
      else if ("define".equals(input)) System.out.println(definition);
      else if (input.startsWith("pick nth ")) pickNthKeyContainingFragment(input); // pick nth key containing fragment
      else if (input.startsWith("pick ")) reloadKey(input.substring("pick ".length())); // explanation?
      else if (input.contains("?")) System.out.println(ai.getAnswer(input));
      else evaluateAI(input);
    }
  }

  private void evaluateAI(String input) {
    System.out.println(ai.getAnswer(
        "is this a good keyword and definition: " + key + " = " + input +
            ". \n1 - Evaluate the answer by answering a question \"Does this capture the essence?\" (keeping in mind, that definition is allowed to be concise, minimalistic and does not require details). If, it does capture the essence the evaluation should be 8/10 and above." +
            "\nand 2 - If evaluations is less than 7/10 - conclude the right answer. Else do not provide the right answer." +
            "\nYour entire answer should be up to 300 characters"));
  }

  private void pickNthKeyContainingFragment(String input) {
    String fragmentToSearchForAndNumber = input.substring("pick nth ".length());
    String endsWithSpaceDigitsPattern = "^(.*) (\\d+)$";

    if (!fragmentToSearchForAndNumber.matches(endsWithSpaceDigitsPattern)) {
      System.out.println("no match");
      reloadKey(input.substring("pick nth ".length()));
    }
    else {
      String fragment = fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$1");
      int nth = Integer.parseInt(fragmentToSearchForAndNumber.replaceAll(endsWithSpaceDigitsPattern, "$2"));
      System.out.println(fragment + " " + nth);
      reloadKey(fragment, nth);
    }
  }

  private void printAllKeyValuesContainingFragment(String fragment) {
    System.out.println("defining all key-values containing fragment: " + fragment);
    functions.printAllEntriesContainingFragmentInKeyValue(fragment);
  }

  private void printAllKeysContainingFragment(String fragment) {
    System.out.println("defining all keys containing fragment: " + fragment);
    functions.printAllEntriesContainingFragmentInKey(fragment);
  }


  private void reloadKey() {
    keyDefinition = functions.pickRandomKeyDefinition();
    key = keyDefinition.getKey();
    definition = keyDefinition.getValue();
  }

  private void reloadKey(String fragment) {
    keyDefinition = functions.pickKeyDefinition(fragment);
    key = keyDefinition.getKey();
    definition = keyDefinition.getValue();
  }

  private void reloadKey(String fragment, int nth) {
    keyDefinition = functions.pickNthKeyDefinition(fragment, nth);
    key = keyDefinition.getKey();
    definition = keyDefinition.getValue();
  }

  private static void printMenu() {
    System.out.println("skip - reloads key;\ndefine - defines current key;\ndefine all <fragment> - defines all keys containing fragment;\ndefine all all <fragment> - defines all keys and values containing fragment;\npick nth <fragment nth> - pick nth key containing fragment;\npick <fragment> - pick key containing fragment;\n<fragment ?> - include question mark to get an answer;");
  }
}
