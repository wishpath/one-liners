package org.sa;

import org.sa.a_config.Props;
import org.sa.console.ColoredString;
import org.sa.console.Colors;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.service.*;

import java.io.IOException;
import java.util.Scanner;

public class Menu {

  private Scanner scanner = new Scanner(System.in);
  private ConceptsLoader concepts = new ConceptsLoader();
  private ScoreService scoreService = new ScoreService(concepts);
  private NotTodayService notTodayService = new NotTodayService(concepts);
  private ConceptPicker pick = new ConceptPicker(concepts, notTodayService);
  private InfoPrinter info = new InfoPrinter(concepts, notTodayService);
  private AiService aiService = new AiService(pick, scoreService, notTodayService);

  private ConceptDTO previousConcept = pick.pickConceptWithLowestScore();
  private ConceptDTO concept = previousConcept;
  private String input = "";

  private static final String MENU = Colors.BLUE + "Menu:" + Colors.RESET +
    MenuLine.string("define", "defines current key") +
    MenuLine.string("define all <fragment>", "defines all keys containing fragment") +
    MenuLine.string("pick nth <fragment nth>", "picks nth key containing fragment") +
    MenuLine.string("pick <fragment>", "picks key containing fragment") +
    MenuLine.string("<fragment ?>", "type something with \"?\" to get an answer") +
    MenuLine.string("all keys", "lists all the keys in this app") +
    MenuLine.string("prev, previous", "comes back to previous concept") +
    MenuLine.string("idk", "marks 'I don't know' to current concept question") +
    MenuLine.string("not today print", "prints concepts that are not to be tested today") +
    MenuLine.string("not today add", "adds concepts that are not to be learned today") +
    MenuLine.string("skip", "reloads key") +
    MenuLine.string("score", "prints current concept score") +
    MenuLine.string("scores", "prints all non-zero scores") +
    MenuLine.string("menu", "prints command menu") + "\n";

  public Menu() throws IOException {
    info.printAllCurrentScores();
    System.out.println(MENU);
    while (true) {
      scoreService.saveScores_OverwriteFile();
      concept.printUserInstruction();
      input = scanner.nextLine().trim();

      if (input.isEmpty())
        concept.printUserInstruction();

      else if ("menu".equals(input))
        System.out.println(MENU);

      else if ("all keys".equals(input) || "keys".equals(input))
        info.printAllKeys();

      else if ("previous".equals(input) || "prev".equals(input))
        setConcept(previousConcept);

      else if (input.startsWith("define all "))
        info.printAllConceptsContainingFragment(sub("define all "));

      else if ("define".equals(input))
        System.out.println(Colors.BLUE + concept.key + ": " + Colors.RESET + concept.definition + "\n");

      else if (input.startsWith("pick nth"))
        setConcept(pick.pickNthConceptWithFragmentInKey(input));

      else if (input.startsWith("pick "))
        setConcept(pick.pickConceptWithFragmentInKey(sub("pick ")));

      else if (input.contains("?"))
        aiService.askAi(input);

      else if ("idk".equals(input)) {
        scoreService.incrementScore(concept, -1);
        SimpleColorPrint.blue("Concept has received a score of -1: ");
        SimpleColorPrint.red(Props.TAB + concept.key + ": " + concept.definition + "\n");
        notTodayService.dontLearnThisToday(concept);
        setConcept(pick.pickConceptWithLowestScore());
      }

      else if ("not today print".equals(input))
        info.printNotTodayConcepts();

      else if ("not today add".equals(input) || "skip".equals(input)) {
        notTodayService.dontLearnThisToday(concept);
        setConcept(pick.pickConceptWithLowestScore());
      }

      else if (input.startsWith("not today"))
        System.out.println("'not today print' or 'not today add'?");

      else if ("score".equals(input))
        concept.printScore();

      else if ("scores".equals(input))
        info.printAllCurrentScores();

      else {
        ConceptDTO conceptToSet = aiService.evaluateUserExplanationWithAI(concept, input);
        setConcept(conceptToSet);
      }

    }
  }

  private void setConcept(ConceptDTO newConcept) {
    if (newConcept.equals(concept)) return;
    previousConcept = concept;
    concept = newConcept;
  }

  private String sub(String skippingThis) {
    return input.substring(skippingThis.length());
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
