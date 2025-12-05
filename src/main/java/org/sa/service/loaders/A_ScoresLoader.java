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

    //load scores to type 'Properties'
    Properties scoreProperties = new Properties();
    try (Reader reader = Files.newBufferedReader(Paths.SCORE_PATH, StandardCharsets.UTF_8)) {
      scoreProperties.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    for (Map.Entry<Object, Object> entryOfKey_score : scoreProperties.entrySet()) {
      String key = (String) entryOfKey_score.getKey();
      int score = Integer.parseInt((String) entryOfKey_score.getValue());

      if (!key_concept.containsKey(key)) {
        System.out.println("Detected deleted key, that has score. Deleted key: " + Colors.RED + key + Colors.RESET);
        continue;
      }

      //score added
      key_score.put(key, score);
      //assign score to object
      key_concept.get(key).score = score;
      //TODO: can add to score_keyList here as well
    }

    TreeMap<Integer, List<String>> score_keyList = new TreeMap<>(); //auto ascending map

    //keys not having explicit score, map to score 0
    for (String key : key_concept.keySet())
      if (key_score.get(key) == null) {
        score_keyList.computeIfAbsent(0, k -> new ArrayList<>()).add(key);
      }

    //sort (map to values) non 0 scores
    for (Map.Entry<String, Integer> e : key_score.entrySet()) {
      score_keyList.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
    }

    return new Object[]{key_score, score_keyList};
  }
}
