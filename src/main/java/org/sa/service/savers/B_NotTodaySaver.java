package org.sa.service.savers;

import org.sa.other.ValueAscendingMap;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Map;

public class B_NotTodaySaver {
  public static void autosaveNotTodayMapToFile(ValueAscendingMap<String, LocalDateTime> notTodayKey_time) {
    try (BufferedWriter writer = Files.newBufferedWriter(org.sa.config.Paths.NOT_TODAY_FILEPATH)) {
      for (Map.Entry<String, LocalDateTime> entry : notTodayKey_time.entrySet())
        writer.write(entry.getKey() + "," + entry.getValue() + System.lineSeparator()); //overwrites
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
