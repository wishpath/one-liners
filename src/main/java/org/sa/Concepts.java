package org.sa;

import org.sa.dto.ConceptDTO;
import org.sa.service.A_ConceptsLoader;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Concepts {


  public Map<String, ConceptDTO> key_concept = A_ConceptsLoader.loadConceptsCheckRepeated();
  public final TreeMap<Integer, Set<String>> score_keySet = mapScores(key_concept);


  public static TreeMap<Integer, Set<String>> mapScores(Map<String, ConceptDTO> key_concept) {
    TreeMap<Integer, Set<String>> score_keyList = new TreeMap<>(); //auto ascending map
    for (ConceptDTO c : key_concept.values()) score_keyList.computeIfAbsent(c.score, k -> new HashSet<>()).add(c.key);
    return score_keyList;
  }
}
