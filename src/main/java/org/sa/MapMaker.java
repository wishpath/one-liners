package org.sa;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MapMaker {
  private static Map<String, String> keyDefinition = createKeyDefinitionMap(new OneLiners().getAllOneLiners());

  public static Map<String, String> getKeyDefinition() {
    return keyDefinition;
  }

  public static Map.Entry<String, String> pickRandomKeyDefinition() {
    return keyDefinition.entrySet()
        .stream()
        .skip(new Random().nextInt(keyDefinition.size()))
        .findFirst()
        .orElse(null);
  }

  public static void main(String[] args) {
    keyDefinition.entrySet().forEach(System.out::println);
  }

  private static Map<String, String> createKeyDefinitionMap(String allOneLiners) {
    return Arrays.stream(allOneLiners.split("\n"))
                 .map(MapMaker::extractEntryFromLine)
                 .sorted(Comparator.comparing(Map.Entry::getKey))
                 .collect(Collectors.toMap(
                   Map.Entry::getKey,
                   Map.Entry::getValue,
                   MapMaker::handleDuplicateKeys
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



}
