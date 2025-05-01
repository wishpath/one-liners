package org.sa.other;

import org.sa.Dictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class OneLinersToPropertiesTransporter {


  private static Map<String, String> createKeyDefinitionMap(String allOneLiners) {
    return Arrays.stream(allOneLiners.split("\n"))
        .filter(line -> line.contains(" - "))
        .map(OneLinersToPropertiesTransporter::extractEntryFromLine)
        .sorted(Comparator.comparing(Map.Entry::getKey))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            OneLinersToPropertiesTransporter::handleDuplicateKeys
        ));
  }

  private static Map.Entry<String, String> extractEntryFromLine(String line) {
    int delimiterIndex = line.indexOf(" - ");
    if (delimiterIndex == -1) {
      throw new IllegalArgumentException("String does not contain the delimiter: " + line);
    }
    String key = line.substring(0, delimiterIndex);
    String value = line.substring(delimiterIndex + 3);
    return Map.entry(key, value);
  }

  private static String handleDuplicateKeys(String existingValue, String newValue) {
    System.out.println("Duplicate key found, skipping entry with key: " + existingValue);
    return existingValue;
  }

  public static void main(String[] args) throws IOException {
    //prep
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    String s = Dictionary.lithuanianRare;
    Path path = Paths.get("src/main/java/org/sa/dictionary/lithuanianRare.properties");
    System.out.println(s.chars().filter(c -> c == '\n').count() + " lines in the string");
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    Properties props = new Properties();
    props.putAll(createKeyDefinitionMap(s));

    //write
    props.store(Files.newBufferedWriter(path), "Generated");
    System.out.println(Files.lines(path).filter(line -> line.contains("=")).count() + " lines in the file");
  }
}
