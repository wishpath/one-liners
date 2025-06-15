package org.sa;

import org.sa.actions.Actions;
import org.sa.actions.Info;
import org.sa.concepts.Concepts;
import org.sa.console.SimpleColorPrint;
import org.sa.other.MenuLine;

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

  private Scanner scanner = new Scanner(System.in);
  private Concepts concepts = new Concepts();
  private Actions act = new Actions(concepts, new AiClient());
  private Info info = new Info(concepts);
  private Map.Entry<String, String> previousConcept = act.pickConceptWithLowestScore();
  private Map.Entry<String, String> concept = previousConcept;
  private String input = "";

  private static final String MENU =
    MenuLine.string("skip", "reloads key") +
    MenuLine.string("define", "defines current key") +
    MenuLine.string("define all <fragment>", "defines all keys containing fragment") +
    MenuLine.string("define all all <fragment>", "defines all keys and values containing fragment") +
    MenuLine.string("pick nth <fragment nth>", "picks nth key containing fragment") +
    MenuLine.string("pick <fragment>", "picks key containing fragment") +
    MenuLine.string("<fragment ?>", "includes question mark to get an answer") +
    MenuLine.string("all keys", "lists all the keys in this app") +
    MenuLine.string("prev, previous", "comes back to previous concept") +
    MenuLine.string("idk", "marks 'I don't know' to current concept question") +
    MenuLine.string("weak, weakness, weaknesses", "prints newest failed entries") +
    MenuLine.string("score", "prints current concept score") +
    MenuLine.string("scores", "prints all non-zero scores") +
    MenuLine.string("menu", "prints command menu") + "\n";

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
