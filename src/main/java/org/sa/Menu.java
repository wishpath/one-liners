package org.sa;

import org.sa.actions.Actions;
import org.sa.actions.Info;
import org.sa.concepts.Concepts;
import org.sa.config.Props;
import org.sa.console.ColoredString;
import org.sa.console.Colors;
import org.sa.service.AdditionalInstructionsToEvaluate;
import org.sa.service.InstructionTextForAi;

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
  private AdditionalInstructionsToEvaluate instructionsToEvaluate = new AdditionalInstructionsToEvaluate(concepts);
  private Actions act = new Actions(concepts, instructionsToEvaluate);
  private Info info = new Info(concepts);
  private Map.Entry<String, String> previousConcept = act.pickConceptWithLowestScore();
  private Map.Entry<String, String> concept = previousConcept;
  private String input = "";

  private static final String MENU = Colors.BLUE + "Menu:" + Colors.RESET +
    MenuLine.string("define", "defines current key") +
    MenuLine.string("define all <fragment>", "defines all keys containing fragment") +
    MenuLine.string("define all all <fragment>", "defines all keys and values containing fragment") +
    MenuLine.string("pick nth <fragment nth>", "picks nth key containing fragment") +
    MenuLine.string("pick <fragment>", "picks key containing fragment") +
    MenuLine.string("<fragment ?>", "includes question mark to get an answer") +
    MenuLine.string("all keys", "lists all the keys in this app") +
    MenuLine.string("prev, previous", "comes back to previous concept") +
    MenuLine.string("idk", "marks 'I don't know' to current concept question") +
    MenuLine.string("weak", "prints concepts with lowest score") +
    MenuLine.string("weak keys", "prints newest failed keys (no definitions)") +
    MenuLine.string("not today print", "prints concepts that are not to be learned today") +
    MenuLine.string("not today add", "adds concepts that are not to be learned today") +
    MenuLine.string("skip", "reloads key") +
    MenuLine.string("score", "prints current concept score") +
    MenuLine.string("scores", "prints all non-zero scores") +
    MenuLine.string("menu", "prints command menu") + "\n";

  public Menu() throws IOException {
    System.out.println(MENU);
    while (true) {
      act.save();
      input = scanner.nextLine().trim();

      if (input.isEmpty())
        continue;

      else if ("menu".equals(input))
        System.out.println(MENU);

      else if ("all keys".equals(input) || "keys".equals(input))
        info.printAllKeys();

      else if ("previous".equals(input) || "prev".equals(input))
        setConcept(previousConcept);

      else if (input.startsWith("define all all "))
        info.printAllConceptsContainingFragmentInKeyValue(sub("define all all "));

      else if (input.startsWith("define all "))
        info.printAllConceptsContainingFragmentInKey(sub("define all "));

      else if ("define".equals(input))
        System.out.println(Colors.BLUE + concept.getKey() + ": " + Colors.RESET + concept.getValue() + "\n");

      else if (input.startsWith("pick nth"))
        setConcept(act.pickNthConceptWithFragmentInKey(input));

      else if (input.startsWith("pick "))
        setConcept(act.pickConceptWithFragmentInKey(sub("pick ")));

      else if (input.contains("?"))
        act.askAi(input);

      else if ("idk".equals(input))
        setConcept(act.answerIDontKnow(concept));

      else if ("weak".equals(input))
        info.printLowestScoreConcepts();

      else if ("weak keys".equals(input))
        info.printNotTodayKeysByTimeAvailableAndLowestScore2();

      else if ("not today print".equals(input))
        info.printNotTodayConcepts();

      else if ("not today add".equals(input) || "skip".equals(input))
        setConcept(act.addKeywordToNotToday(concept));

      else if (input.startsWith("not today"))
        System.out.println("'not today print' or 'not today add'?");

      else if ("score".equals(input))
        info.printCurrentKeyScore(concept);

      else if ("scores".equals(input))
        info.printAllNonZeroScores();

      else
        setConcept(act.evaluateUserExplanationWithAI(concept, input, InstructionTextForAi.getInstructionToEvaluateUserInput(concept, instructionsToEvaluate, input))); //if evaluation < 7, keeps same concept;
    }
  }
}

class MenuLine {
  public static String string(String command, String function) {
    return
        "\n" +
            Props.TAB + ColoredString.red(command) +
            " - " + function;
  }
}
