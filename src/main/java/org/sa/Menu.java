package org.sa;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Menu {

  private static final String MENU = "skip - reloads key;\ndefine - defines current key;\ndefine all <fragment> - defines all keys containing fragment;\ndefine all all <fragment> - defines all keys and values containing fragment;\npick nth <fragment nth> - pick nth key containing fragment;\npick <fragment> - pick key containing fragment;\n<fragment ?> - include question mark to get an answer;";
  private Scanner scanner = new Scanner(System.in);
  private Actions act = new Actions();
  private Map.Entry<String, String> concept = act.pickRandomConcept();
  private String input = "";
 


  public Menu() throws IOException {
    while (true) {
      System.out.println("Explain this: " + concept.getKey());
      input = scanner.nextLine().trim();

      if (input.isEmpty()) 
        continue;
      
      else if ("menu".equals(input)) 
        System.out.println(MENU);
      
      else if ("skip".equals(input) || "s".equals(input)) 
        concept = act.pickRandomConcept();
      
      else if ("all keys".equals(input)) 
        act.printAllKeys();
      
      else if (input.startsWith("define all all ")) 
        act.printAllConceptsContainingFragmentInKeyValue(sub("define all all "));
      
      else if (input.startsWith("define all ")) 
        act.printAllConceptsContainingFragmentInKey(sub("define all "));
      
      else if ("define".equals(input)) 
        System.out.println(concept.getValue());
      
      else if (input.startsWith("pick nth ")) 
        concept = act.pickNthConceptWithFragmentInKey(input);
      
      else if (input.startsWith("pick "))
        concept = act.pickConceptWithFragmentInKey(sub("pick "));
      
      else if (input.contains("?")) 
        act.askAi(input);
      
      else 
        act.evaluateUserExplanationWithAI(concept.getKey(), input);
    }
  }

  private String sub(String skippingThis) {
    return input.substring(skippingThis.length());
  }
}
