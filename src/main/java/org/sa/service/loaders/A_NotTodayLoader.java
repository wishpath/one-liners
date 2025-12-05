package org.sa.service.loaders;

import org.sa.config.Paths;
import org.sa.config.Props;
import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;
import org.sa.other.ValueAscendingMap;
import org.sa.service.savers.B_NotTodaySaver;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;


public class A_NotTodayLoader {
  public static ValueAscendingMap<String, LocalDateTime> loadNotTodayConcepts(Map<String, ConceptDTO> key_concept) {

    ValueAscendingMap<String, LocalDateTime> notTodayKey_time = new ValueAscendingMap<>();//keys skipped from learning for one day

    try (Stream<String> lines = Files.lines(Paths.NOT_TODAY_FILEPATH)) {
      LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
      lines.map(line -> line.split(","))
          .peek(linePartsArr -> {
            SimpleColorPrint.normal(Props.TAB + Arrays.toString(linePartsArr));
            if (linePartsArr.length > 2) throw new RuntimeException("LINE CONTAINS TOO MANY COMMAS");
          })
          .map(parts -> Map.entry(parts[0], LocalDateTime.parse(parts[1])))
          .filter(e -> e.getValue().isAfter(oneDayAgo))
          .filter(e -> key_concept.containsKey(e.getKey()))
          .forEach(e -> notTodayKey_time.put(e.getKey(), e.getValue()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    B_NotTodaySaver.autosaveNotTodayMapToFile(notTodayKey_time);

    return notTodayKey_time;
  }
}
