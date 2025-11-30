package org.sa.service.loaders;

import org.sa.config.Paths;
import org.sa.dto.ConceptDTO;
import org.sa.other.ValueAscendingMap;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

public class A_ScoresLoader {
  public static ValueAscendingMap<String, Integer> loadScores(Map<String, ConceptDTO> key_concept) throws IOException {
    ValueAscendingMap<String, Integer> key_score = new ValueAscendingMap<>(); //from now on keys with explicit score 0 as well

    //load score to type 'Properties'
    Properties scoreProperties = new Properties();
    try (Reader reader = Files.newBufferedReader(Paths.SCORE_PATH, StandardCharsets.UTF_8)) {
      scoreProperties.load(reader);
    }

    for (Map.Entry<Object, Object> entryOfKey_score : scoreProperties.entrySet()) {
      String key = (String) entryOfKey_score.getKey();
      int score = Integer.parseInt((String) entryOfKey_score.getValue());
      if (!key_concept.containsKey(key)) continue; // has score but key got deleted/ altered
      key_score.put(key, score);
      key_concept.get(key).score = score;
    }

    return key_score;
  }
}
