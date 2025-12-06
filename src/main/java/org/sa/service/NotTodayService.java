package org.sa.service;

import org.sa.a_config.FilePath;
import org.sa.dto.ConceptDTO;
import org.sa.z_data_structure.ValueAscendingMap;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

public class NotTodayService {
  public ValueAscendingMap<String, LocalDateTime> notTodayKey_time;//keys skipped from learning for one day
  private Map<String, ConceptDTO> key_concept;

  public NotTodayService(ConceptsLoader concepts) {
    this.key_concept = concepts.key_concept;
    this.notTodayKey_time = loadNotTodayConcepts();
  }

  public void autosaveNotTodayMapToFile(ValueAscendingMap<String, LocalDateTime> notTodayKey_time) {
    try (BufferedWriter writer = Files.newBufferedWriter(FilePath.NOT_TODAY_FILEPATH)) {
      for (Map.Entry<String, LocalDateTime> entry : notTodayKey_time.entrySet())
        writer.write(entry.getKey() + "," + entry.getValue() + System.lineSeparator()); //overwrites
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ValueAscendingMap<String, LocalDateTime> loadNotTodayConcepts() {

    ValueAscendingMap<String, LocalDateTime> notTodayKey_time = new ValueAscendingMap<>();//keys skipped from learning for one day

    try (Stream<String> lines = Files.lines(FilePath.NOT_TODAY_FILEPATH)) {
      LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
      lines.map(line -> line.split(","))
          .peek(linePartsArr -> {
            if (linePartsArr.length > 2) throw new RuntimeException("LINE CONTAINS TOO MANY COMMAS");
          })
          .map(parts -> Map.entry(parts[0], LocalDateTime.parse(parts[1])))
          .filter(e -> e.getValue().isAfter(oneDayAgo))
          .filter(e -> key_concept.containsKey(e.getKey()))
          .forEach(e -> notTodayKey_time.put(e.getKey(), e.getValue()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    autosaveNotTodayMapToFile(notTodayKey_time);

    return notTodayKey_time;
  }

  public void dontLearnThisToday(ConceptDTO concept){
    refreshNotTodayMap(); //remove entries older than one day
    notTodayKey_time.put(concept.key, LocalDateTime.now());
    autosaveNotTodayMapToFile(notTodayKey_time);
  }

  public void refreshNotTodayMap() {
    LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
    notTodayKey_time.entrySet().removeIf(e -> e.getValue().isBefore(oneDayAgo));
  }
}
