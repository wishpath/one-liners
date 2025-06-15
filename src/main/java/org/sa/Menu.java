package org.sa;

import org.sa.actions.Actions;
import org.sa.actions.Info;
import org.sa.concepts.Concepts;
import org.sa.console.SimpleColorPrint;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Menu {

  private void setConcept(Map.Entry<String, String> newConcept) {
    if (newConcept.equals(concept)) return;
    previousConcept = concept;
    concept = newConcept;
  }

  private String sub(String skippingThis) {
    return input.substring(skippingThis.length());
  }
  private static final String MENU =
      "\n\u001B[31mskip\u001B[0m - reloads key;\n" +
          "\u001B[31mdefine\u001B[0m - defines current key;\n" +
          "\u001B[31mdefine all <fragment>\u001B[0m - defines all keys containing fragment;\n" +
          "\u001B[31mdefine all all <fragment> \u001B[0m- defines all keys and values containing fragment;\n" +
          "\u001B[31mpick nth <fragment nth> \u001B[0m- pick nth key containing fragment;\n" +
          "\u001B[31mpick <fragment> \u001B[0m- pick key containing fragment;\n" +
          "\u001B[31m<fragment ?> \u001B[0m- include question mark to get an answer;\n" +
          "\u001B[31mall keys \u001B[0m- lists all the keys in this app;\n" +
          "\u001B[31mweak, weakness, weaknesses \u001B[0m- prints newest failed entries;\n" +
          "\u001B[31mscore \u001B[0m- prints current concept score;\n" +
          "\u001B[31mscores \u001B[0m- prints all non-zero scores;\n";

  private Scanner scanner = new Scanner(System.in);
  private Concepts concepts = new Concepts();
  private Actions act = new Actions(concepts, new AiClient());
  private Info info = new Info(concepts);
  private Map.Entry<String, String> previousConcept = act.pickConceptWithLowestScore();
  private Map.Entry<String, String> concept = previousConcept;
  private String input = "";



  public Menu() throws IOException {
    System.out.println(MENU);
    while (true) {
      act.save();
      SimpleColorPrint.blueInLine("Please explain this: ");
      SimpleColorPrint.red(concept.getKey() + "\n");
      input = scanner.nextLine().trim();

      if (input.isEmpty())
        continue;

      else if ("menu".equals(input))
        System.out.println(MENU);

      else if ("skip".equals(input) || "s".equals(input))
        setConcept(act.pickConceptWithLowestScore());

      else if ("all keys".equals(input))
        info.printAllKeys();

      else if ("previous".equals(input) || "prev".equals(input))
        setConcept(previousConcept);

      else if (input.startsWith("define all all "))
        info.printAllConceptsContainingFragmentInKeyValue(sub("define all all "));

      else if (input.startsWith("define all "))
        info.printAllConceptsContainingFragmentInKey(sub("define all "));

      else if ("define".equals(input))
        System.out.println(concept.getValue() + "\n");

      else if (input.startsWith("pick nth "))
        setConcept(act.pickNthConceptWithFragmentInKey(input));

      else if (input.startsWith("pick "))
        setConcept(act.pickConceptWithFragmentInKey(sub("pick ")));

      else if (input.contains("?"))
        act.askAi(input);

      else if ("idk".equals(input))
        setConcept(act.answerIDontKnow(concept));

      else if ("weak".equals(input) || "weakness".equals(input) || "weaknesses".equals(input))
        info.printEntriesWithMinusScore();

      else if ("score".equals(input))
        info.printCurrentKeyScore(concept);

      else if ("scores".equals(input))
        info.printAllNonZeroScores();

      else
        setConcept(act.evaluateUserExplanationWithAI(concept, input)); //if evaluation < 7, keeps same concept;
    }
  }
}
