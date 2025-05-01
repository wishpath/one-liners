package org.sa;

import org.sa.concepts.Concepts;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class Functions {

  Concepts concepts = new Concepts();

  public Functions() throws IOException {
  }

  public Map.Entry<String, String> pickRandomKeyDefinition() {
    return concepts.map.entrySet()
        .stream()
        .skip(new Random().nextInt(concepts.map.size()))
        .findFirst()
        .orElse(null);
  }

  public Map.Entry<String, String> pickKeyDefinition(String fragment) {
    return concepts.map.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .findFirst()
        .orElse(pickRandomKeyDefinition());
  }


  public Map.Entry<String, String> pickNthKeyDefinition(String fragment, int nthInstance) {
    return concepts.map.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .skip(nthInstance + 1)
        .findFirst()
        .orElse(pickRandomKeyDefinition());
  }

  public void printAllEntriesContainingFragmentInKey(String fragment) {
    concepts.map.entrySet()
        .stream()
        .filter(entry -> entry.getKey().toLowerCase().contains(fragment.toLowerCase()))
        .forEach(entry -> {
          System.out.println("\n" + entry.getKey() + ":");
          System.out.println(entry.getValue());
        });
    System.out.println();
  }

  public void printAllEntriesContainingFragmentInKeyValue(String fragment) {
    concepts.map.entrySet()
        .stream()
        .filter(entry -> (entry.getKey() + " " + entry.getValue()).toLowerCase().contains(fragment.toLowerCase()))
        .forEach(entry -> {
          System.out.println("\n" + entry.getKey() + ":");
          System.out.println(entry.getValue());
        });
    System.out.println();
  }

  public void printAllKeys() {
    System.out.println("The list of all keys:");
    concepts.map.entrySet()
        .stream()
        .forEach(entry -> {
          System.out.print(entry.getKey() + ", ");
        });
    System.out.println();
  }
}
