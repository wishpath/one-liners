package org.sa;

import java.util.Map;
import java.util.Scanner;

public class ScannerApp {

  private static final String EDIT = "edit: ";
  private Scanner scanner = new Scanner(System.in);
  private MapMaker map = new MapMaker();
  private AiClient ai = new AiClient();
  private Map.Entry<String, String> keyDefinition = map.pickRandomKeyDefinition();
  private String key = keyDefinition.getKey();
  private String definition = keyDefinition.getValue();

  public ScannerApp() {
    while (true) {
      System.out.println("Explain this: " + key);
      String input = scanner.nextLine().trim();
      if (input.isEmpty()) continue;
      else if ("menu".equals(input)) printMenu();
      else if ("skip".equals(input) || "s".equals(input)) {
        reloadKey();
      } else if (input.startsWith("define all all ")) {
        printAllKeyValuesContainingFragment(input.substring("define all all ".length()));
      } else if (input.startsWith("define all ")) {
        printAllKeysContainingFragment(input.substring("define all ".length()));
      } else if ("define".equals(input)) System.out.println(definition);
//      else if (input.startsWith(EDIT)) {

//        printDefinition();
//        map.edit(keyDefinition, input.substring(6));
//      }

      else if (input.startsWith("pick nth ")) {
        String searchFor = input.substring("pick nth ".length());
        String endsWithSpaceDigitsPattern = "^(.*) (\\d+)$";
        if (!searchFor.matches(endsWithSpaceDigitsPattern)) {
          System.out.println("no match");
          reloadKey(input.substring("pick nth ".length()));
        } else {
          String fragment = searchFor.replaceAll(endsWithSpaceDigitsPattern, "$1");
          int nth = Integer.parseInt(searchFor.replaceAll(endsWithSpaceDigitsPattern, "$2"));
          System.out.println(fragment + " " + nth);
          reloadKey(fragment, nth);
        }
      } else if (input.startsWith("pick ")) reloadKey(input.substring("pick ".length()));
      else if (input.contains("?")) System.out.println(ai.getAnswer(input));
      else {
        System.out.println(ai.getAnswer(
            "is this a good keyword and definition: " + key + " = " + input +
                ". \n1 - Evaluate the answer by answering a question \"Does this capture the essence?\" (keeping in mind, that definition is allowed to be concise, minimalistic and does not require details). If, it does capture the essence the evaluation should be 8/10 and above." +
                "\nand 2 - If evaluations is less than 7/10 - conclude the right answer. Else do not provide the right answer." +
                "\nYour entire answer should be up to 300 characters"));
      }
    }
  }

  private void printAllKeyValuesContainingFragment(String fragment) {
    System.out.println("defining all key-values containing fragment: " + fragment);
    new MapMaker().printAllEntriesContainingFragmentInKeyValue(fragment);
  }

  private void printAllKeysContainingFragment(String fragment) {
    System.out.println("defining all keys containing fragment: " + fragment);
    new MapMaker().printAllEntriesContainingFragmentInKey(fragment);
  }


  private void reloadKey() {
    keyDefinition = new MapMaker().pickRandomKeyDefinition();
    key = keyDefinition.getKey();
    definition = keyDefinition.getValue();
  }

  private void reloadKey(String fragment) {
    keyDefinition = new MapMaker().pickKeyDefinition(fragment);
    key = keyDefinition.getKey();
    definition = keyDefinition.getValue();
  }

  private void reloadKey(String fragment, int nth) {
    keyDefinition = new MapMaker().pickNthKeyDefinition(fragment, nth);
    key = keyDefinition.getKey();
    definition = keyDefinition.getValue();
  }

  private void printDefinition() {
    System.out.println(keyDefinition.getValue());
  }

  private static void printMenu() {
    System.out.println("skip - reloads key;\ndefine - defines current key;\ndefine all <fragment> - defines all keys containing fragment;\ndefine all all <fragment> - defines all keys and values containing fragment;\npick nth <fragment nth> - pick nth key containing fragment;\npick <fragment> - pick key containing fragment;\n<fragment ?> - include question mark to get an answer;");
  }
}
