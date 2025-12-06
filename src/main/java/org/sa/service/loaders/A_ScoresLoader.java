package org.sa.service.loaders;

import org.sa.A_config.Paths;
import org.sa.console.Colors;
import org.sa.dto.ConceptDTO;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class A_ScoresLoader {
  public static TreeMap<Integer, Set<String>> loadAssignAndMapScores(Map<String, ConceptDTO> key_concept) {

    TreeMap<Integer, Set<String>> score_keyList = new TreeMap<>(); //auto ascending map

    //load scores to type 'Properties'
    Properties scoreProperties = new Properties();
    try (Reader reader = Files.newBufferedReader(Paths.SCORE_PATH, StandardCharsets.UTF_8)) {
      scoreProperties.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //assign scores to conceptDTOs
    for (Map.Entry<Object, Object> entryOfKey_scoreProperties : scoreProperties.entrySet()) {
      String propertiesKey = (String) entryOfKey_scoreProperties.getKey();
      int propertiesScore = Integer.parseInt((String) entryOfKey_scoreProperties.getValue());
      if (!key_concept.containsKey(propertiesKey)) {
        System.err.println("Detected deleted key, that has score. Deleted key: " + Colors.RED + propertiesKey + Colors.RESET);
        continue;
      }
      key_concept.get(propertiesKey).score = propertiesScore;
      score_keyList.computeIfAbsent(propertiesScore, k -> new HashSet<>()).add(propertiesKey);
    }

    //map scores to score_keyList
    for (String key : key_concept.keySet()) score_keyList.computeIfAbsent(0, k -> new HashSet<>()).add(key);

    return score_keyList;
  }
}
