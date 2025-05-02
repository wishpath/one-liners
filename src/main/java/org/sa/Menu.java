package org.sa;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Menu {

  private static final String MENU = "skip - reloads key;\ndefine - defines current key;\ndefine all <fragment> - defines all keys containing fragment;\ndefine all all <fragment> - defines all keys and values containing fragment;\npick nth <fragment nth> - pick nth key containing fragment;\npick <fragment> - pick key containing fragment;\n<fragment ?> - include question mark to get an answer;";
  private Scanner scanner = new Scanner(System.in);
  private Function act = new Function();
  private Map.Entry<String, String> concept = act.pickRandomConcept();
 


  public Menu() throws IOException {
    while (true) {
      System.out.println("Explain this: " + concept.getKey());
      String input = scanner.nextLine().trim();

      if (input.isEmpty()) 
        continue;
      
      else if ("menu".equals(input)) 
        System.out.println(MENU);
      
      else if ("skip".equals(input) || "s".equals(input)) 
        concept = act.pickRandomConcept();
      
      else if ("all keys".equals(input)) 
        act.printAllKeys();
      
      else if (input.startsWith("define all all ")) 
        act.printAllEntriesContainingFragmentInKeyValue(input.substring("define all all ".length()));
      
      else if (input.startsWith("define all ")) 
        act.printAllEntriesContainingFragmentInKey(input.substring("define all ".length()));
      
      else if ("define".equals(input)) 
        System.out.println(concept.getValue());
      
      else if (input.startsWith("pick nth ")) 
        concept = act.pickNthKeyContainingFragment(input);
      
      else if (input.startsWith("pick "))
        concept = act.pickKeyDefinition(input.substring("pick ".length()));
      
      else if (input.contains("?")) 
        act.askAi(input);
      
      else 
        act.evaluateUserExplanationWithAI(concept.getKey(), input);
    }
  }






}
