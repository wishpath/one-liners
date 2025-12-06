package org.sa.storage.concepts;

import org.sa.dto.ConceptDTO;
import org.sa.service.loaders.A_ConceptsLoader;
import org.sa.service.loaders.A_ScoresLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Concepts {
  public final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/storage/concepts/topics-swed");
  public Map<String, ConceptDTO> key_concept = A_ConceptsLoader.loadConceptsCheckRepeated();
  public final TreeMap<Integer, Set<String>> score_keySet = A_ScoresLoader.loadAssignAndMapScores(key_concept);
}
