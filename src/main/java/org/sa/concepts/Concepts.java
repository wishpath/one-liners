package org.sa.concepts;

import org.sa.dto.ConceptDTO;
import org.sa.other.ValueAscendingMap;
import org.sa.service.loaders.A_ConceptsLoader;
import org.sa.service.loaders.A_ScoresLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Concepts {
  public final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");
  public Map<String, ConceptDTO> key_concept = A_ConceptsLoader.loadConceptsCheckRepeated();
  public final Object[] scores = A_ScoresLoader.loadScores(key_concept);
  public final ValueAscendingMap<String, Integer> key_score = (ValueAscendingMap<String, Integer>) scores[0]; //no keys with score zero, auto ascending
  public final TreeMap<Integer, Set<String>> score_keySet = (TreeMap<Integer, Set<String>>) scores[1]; //auto ascending map
}
