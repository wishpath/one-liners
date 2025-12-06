package org.sa.service;

import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;

import java.util.*;
import java.util.Map.Entry;

public class ConceptPicker {

  private final NotTodayService notTodayService;
  private ConceptsLoader concepts;

  public ConceptPicker(ConceptsLoader concepts, NotTodayService notTodayService) {
    this.notTodayService = notTodayService;
    this.concepts = concepts;
  }

  public ConceptDTO pickConceptWithLowestScore() {
    SimpleColorPrint.blue("Picking concept with lowest score...\n");

    notTodayService.refreshNotTodayMap();
    Set<String> skippableKeys = notTodayService.notTodayKey_time.keySet();

    for (Set<String> keys : concepts.score_keySet.values()) {
      List<String> eligibleKeys = new ArrayList<>();

      for (String key : keys)
        if (!skippableKeys.contains(key))
          eligibleKeys.add(key);

      if (eligibleKeys.isEmpty()) continue;
      String randomKey = eligibleKeys.get(new Random().nextInt(eligibleKeys.size()));
      return concepts.key_concept.get(randomKey);
    }

    throw new NoSuchElementException("No eligible concept available");
  }

  public ConceptDTO pickConceptWithFragmentInKey(String fragment) {
    if (fragment == null || fragment.isEmpty()) {
      SimpleColorPrint.red("Fragment is empty");
      return pickConceptWithLowestScore();
    }

    SimpleColorPrint.blueInLine("Picking key containing fragment: ");
    SimpleColorPrint.red(fragment + "\n");

    return concepts.key_concept.entrySet()
      .stream()
      .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
      .findFirst()
      .map(entry -> {
        SimpleColorPrint.redInLine("Picked key: ");
        SimpleColorPrint.blue(entry.getKey());
        return concepts.key_concept.get(entry.getKey());
      })
      .orElseGet(() -> {
        SimpleColorPrint.blueInLine("Not found any concept containing fragment: ");
        SimpleColorPrint.red(fragment);
        return pickConceptWithLowestScore();
      });
  }

  public ConceptDTO pickNthKeyDefinition(String fragment, int nthInstance) {
    List<Entry<String, ConceptDTO>> matchingKey_Concept = concepts.key_concept
        .entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .toList();

    if (matchingKey_Concept.size() == 0) {
      SimpleColorPrint.redInLine("Not found any concept containing fragment: ");
      SimpleColorPrint.blue(fragment + "\n");
      return pickConceptWithLowestScore();
    }

    if (matchingKey_Concept.size() - 1 < nthInstance) {
      SimpleColorPrint.redInLine("There are only ");
      SimpleColorPrint.blueInLine(String.valueOf(matchingKey_Concept.size()));
      SimpleColorPrint.redInLine(" matches for the fragment ");
      SimpleColorPrint.blueInLine(fragment);
      SimpleColorPrint.redInLine(". The entered 'nth' value is too high: ");
      SimpleColorPrint.blue(String.valueOf(nthInstance) + "\n");
      InfoPrinter.printKeys_fragmentHighlighted(fragment, matchingKey_Concept);
      return pickConceptWithLowestScore();
    }

    InfoPrinter.printKeys_fragmentHighlighted(fragment, matchingKey_Concept);
    return concepts.key_concept.get(matchingKey_Concept.get(nthInstance).getKey());
  }


  public ConceptDTO pickNthConceptWithFragmentInKey(String input) {
    //pick nth <fragment nth> - pick nth key containing fragment;
    String fragmentToSearchForAndNumber = input.substring("pick nth".length()); //should be "<fragment nth>"
    final String ENDS_WITH_SPACE_AND_DIGITS_PATTERN = "^(.*) (\\d+)$";

    if (!fragmentToSearchForAndNumber.matches(ENDS_WITH_SPACE_AND_DIGITS_PATTERN)) {
      SimpleColorPrint.blueInLine("pick nth <fragment nth>, ");
      SimpleColorPrint.redInLine(fragmentToSearchForAndNumber);
      SimpleColorPrint.normal(" - did not end with space and number.");
      SimpleColorPrint.red("'pick nth <fragment nth>' was aborted due to not matching ENDS_WITH_SPACE_AND_DIGITS_PATTERN = \"(.*) (\\d+)$\"");
      return pickConceptWithFragmentInKey(input.substring("pick nth".length()).trim());
    } else {
      String fragment = fragmentToSearchForAndNumber.replaceAll(ENDS_WITH_SPACE_AND_DIGITS_PATTERN, "$1").trim();
      int nth = Integer.parseInt(fragmentToSearchForAndNumber.replaceAll(ENDS_WITH_SPACE_AND_DIGITS_PATTERN, "$2"));
      SimpleColorPrint.blueInLine("Searching for fragment: ");
      SimpleColorPrint.redInLine(fragment);
      SimpleColorPrint.blueInLine(" nth: ");
      SimpleColorPrint.red(String.valueOf(nth));
      return pickNthKeyDefinition(fragment, nth);
    }
  }
}

