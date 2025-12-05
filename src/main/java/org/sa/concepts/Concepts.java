package org.sa.concepts;

import org.sa.dto.ConceptDTO;
import org.sa.other.ValueAscendingMap;
import org.sa.service.loaders.A_ConceptsLoader;
import org.sa.service.loaders.A_NotTodayLoader;
import org.sa.service.loaders.A_ScoresLoader;
import org.sa.service.savers.B_NotTodaySaver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


//purpose: load, keep and manage concept info: key, definition, score, status and their permutations
public class Concepts {

  public static final Path TOPICS_SWED = Paths.get("src/main/java/org/sa/concepts/topics-swed");



  public final Map<String, ConceptDTO> key_concept = A_ConceptsLoader.loadConceptsCheckRepeated();

  //TODO: temporary score structure
  public final Object[] scores = A_ScoresLoader.loadScores(key_concept);
  public final ValueAscendingMap<String, Integer> key_score = (ValueAscendingMap<String, Integer>) scores[0]; //no keys with score zero, auto ascending
  public final TreeMap<Integer, List<String>> score_keyList = (TreeMap<Integer, List<String>>) scores[1]; //auto ascending map

  public final ValueAscendingMap<String, LocalDateTime> notTodayKey_time = A_NotTodayLoader.loadNotTodayConcepts(key_concept);//keys skipped from learning for one day

  public void dontLearnThisToday(String key) throws IOException {
    refreshNotTodayMap(); //remove entries older than one day
    notTodayKey_time.put(key, LocalDateTime.now());
    B_NotTodaySaver.autosaveNotTodayMapToFile(notTodayKey_time);
  }

  public void refreshNotTodayMap() {
    LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
    notTodayKey_time.entrySet().removeIf(e -> e.getValue().isBefore(oneDayAgo));
  }
}
