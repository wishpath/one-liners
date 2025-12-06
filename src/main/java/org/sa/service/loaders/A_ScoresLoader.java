package org.sa.service.loaders;

import org.sa.config.Paths;
import org.sa.console.Colors;
import org.sa.dto.ConceptDTO;
import org.sa.other.ValueAscendingMap;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class A_ScoresLoader {
  public static Object[] loadScores(Map<String, ConceptDTO> key_concept) {
    ValueAscendingMap<String, Integer> key_score = new ValueAscendingMap<>(); //from now on keys with explicit score 0 as well
    TreeMap<Integer, Set<String>> score_keyList = new TreeMap<>(); //auto ascending map

    //load scores to type 'Properties'
    Properties scoreProperties = new Properties();
    try (Reader reader = Files.newBufferedReader(Paths.SCORE_PATH, StandardCharsets.UTF_8)) {
      scoreProperties.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    for (Map.Entry<Object, Object> entryOfKey_scoreProperties : scoreProperties.entrySet()) {
      String propertiesKey = (String) entryOfKey_scoreProperties.getKey();
      int propertiesScore = Integer.parseInt((String) entryOfKey_scoreProperties.getValue());

      if (!key_concept.containsKey(propertiesKey)) {
        System.err.println("Detected deleted key, that has score. Deleted key: " + Colors.RED + propertiesKey + Colors.RESET);
        continue;
      }

      //score added
      key_score.put(propertiesKey, propertiesScore);
      //assign score to object
      key_concept.get(propertiesKey).score = propertiesScore;
      score_keyList.computeIfAbsent(propertiesScore, k -> new HashSet<>()).add(propertiesKey);
    }

    //when score is not defined in the properties, it is assigned as 0 as default in the concept constructor. (properties might also contain score of 0)
    for (String key : key_concept.keySet())
      if (key_concept.get(key).score == 0) {
        score_keyList.computeIfAbsent(0, k -> new HashSet<>()).add(key);
      }

    return new Object[]{key_score, score_keyList};
  }
}
