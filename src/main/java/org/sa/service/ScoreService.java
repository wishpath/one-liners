package org.sa.service;

import org.sa.a_config.FilePath;
import org.sa.dto.ConceptDTO;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScoreService {
  private final ConceptsLoader concepts;

  public ScoreService(ConceptsLoader concepts) {
    this.concepts = concepts;
  }

  public void incrementScore(ConceptDTO conceptDTO, int increment) {
    //remove key from score map (from old score)
    Set<String> initialSet = concepts.score_keySet.get(conceptDTO.score);
    if (initialSet.size() == 1) concepts.score_keySet.remove(conceptDTO.score);
    else initialSet.remove(conceptDTO.key);

    //increase score in the datastructures:
    conceptDTO.score += increment;

    //add key back to score map (to new score)
    concepts.score_keySet.computeIfAbsent(conceptDTO.score, k -> new HashSet<>()).add(conceptDTO.key);
  }

  public void saveScores_OverwriteFile() {
    try {
      BufferedWriter writer = Files.newBufferedWriter(FilePath.SCORE_PATH);

      for (Map.Entry<String, ConceptDTO> e : concepts.key_concept.entrySet()) {
        // escapes these characters: space, tab, newline, carriage return, formfeed, '=', ':'
        String escapedKeyForPropertiesFileFormat = e.getKey().replaceAll("([ \\t\\n\\r\\f=:])", "\\\\$1");
        String score = e.getValue().score.toString();
        writer.write( escapedKeyForPropertiesFileFormat + "=" + score + "\n");
      }

      writer.flush();
      writer.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
