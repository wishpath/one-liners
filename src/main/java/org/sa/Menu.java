package org.sa;

import org.sa.actions.Actions;
import org.sa.actions.Info;
import org.sa.concepts.Concepts;
import org.sa.config.Props;
import org.sa.console.ColoredString;
import org.sa.console.Colors;
import org.sa.dto.ConceptDTO;
import org.sa.service.NotTodayService;

import java.io.IOException;
import java.util.Scanner;

public class Menu {

  private void setConcept(ConceptDTO newConcept) {
    if (newConcept.equals(concept)) return;
    previousConcept = concept;
    concept = newConcept;
  }

  private String sub(String skippingThis) {
    return input.substring(skippingThis.length());
  }

  private Scanner scanner = new Scanner(System.in);
  private Concepts concepts = new Concepts();
  private NotTodayService notTodayService = new NotTodayService(concepts);
  private Actions act = new Actions(concepts, notTodayService);
  private Info info = new Info(concepts, notTodayService);
  private ConceptDTO previousConcept = act.pickConceptWithLowestScore();
  private ConceptDTO concept = previousConcept;
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
    info.printAllCurrentScores();
    System.out.println(MENU);
    while (true) {
      act.saveScores_OverwriteFile();
      info.printUserInstruction(concept);
      input = scanner.nextLine().trim();


      if (input.isEmpty())
        info.printUserInstruction(concept);

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
        System.out.println(Colors.BLUE + concept.key + ": " + Colors.RESET + concept.definition + "\n");

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

      else {
        ConceptDTO conceptToSet = act.evaluateUserExplanationWithAI(concept, input); //if evaluation < 7, keeps same concept;
        setConcept(conceptToSet);
      }

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
