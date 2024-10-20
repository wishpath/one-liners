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
      else if ("skip".equals(input) || "".equals(input)) {reloadKey();}
      else if ("define".equals(input)) System.out.println(definition);
//      else if (input.startsWith(EDIT)) {
//        printDefinition();
//        map.edit(keyDefinition, input.substring(6));
//      }
      else if (input.contains("?")) System.out.println(ai.getAnswer(input));
      else {
        System.out.println(ai.getAnswer(
            "is this a good keyword and definition: " + key + " = " + input +
                ". \n1 - Evaluate the answer " +
                "\nand 2 - conclude the right answer. " +
                "\nYour entire answer should be up to 300 characters"));
      }
    }
  }


  private void reloadKey() {
    keyDefinition = new MapMaker().pickRandomKeyDefinition();
    key = keyDefinition.getKey();
    definition = keyDefinition.getValue();
  }

  private void printDefinition() {
    System.out.println(keyDefinition.getValue());
  }
}
