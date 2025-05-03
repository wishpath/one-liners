package org.sa;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Menu {

  private static final String MENU = "\u001B[31mskip\u001B[0m - reloads key;\n\u001B[31mdefine\u001B[0m - defines current key;\n\u001B[31mdefine all <fragment>\u001B[0m - defines all keys containing fragment;\n\u001B[31mdefine all all <fragment> \u001B[0m- defines all keys and values containing fragment;\n\u001B[31mpick nth <fragment nth> \u001B[0m- pick nth key containing fragment;\n\u001B[31mpick <fragment> \u001B[0m- pick key containing fragment;\n\u001B[31m<fragment ?> \u001B[0m- include question mark to get an answer;\n";
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
