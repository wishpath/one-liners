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

  public Map.Entry<String, String> pickKeyDefinition(String fragment) {
    return keyDefinition.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .findFirst()
        .orElse(pickRandomKeyDefinition());
  }


  public Map.Entry<String, String> pickNthKeyDefinition(String fragment, int nthInstance) {
    return keyDefinition.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .skip(nthInstance + 1)
        .findFirst()
        .orElse(pickRandomKeyDefinition());
  }

  public static void main(String[] args) {
    keyDefinition.entrySet().forEach(System.out::println);
  }

  private static Map<String, String> createKeyDefinitionMap(String allOneLiners) {
    return Arrays.stream(allOneLiners.split("\n"))
                 .filter(line -> line.contains(" - "))
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


  public void printAllEntriesContainingFragmentInKey(String fragment) {
    keyDefinition.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .forEach(entry -> {
          System.out.println("\n" + entry.getKey() + ":");
          System.out.println(entry.getValue());
        });
    System.out.println();
  }

  public void printAllEntriesContainingFragmentInKeyValue(String fragment) {
    keyDefinition.entrySet()
        .stream()
        .filter(entry -> (entry.getKey() + " " + entry.getValue()).toLowerCase().contains(fragment.toLowerCase()))
        .forEach(entry -> {
          System.out.println("\n" + entry.getKey() + ":");
          System.out.println(entry.getValue());
        });
    System.out.println();
  }
}
