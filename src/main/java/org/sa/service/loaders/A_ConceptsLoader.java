package org.sa.service.loaders;

import org.sa.console.SimpleColorPrint;
import org.sa.dto.ConceptDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class A_ConceptsLoader {
  public static final Path TOPICS_PUBLIC = Paths.get("src/main/java/org/sa/storage/concepts/topics");

  public static Map<String, ConceptDTO> loadConceptsCheckRepeated(){
    Map<String, ConceptDTO> key_concept = new HashMap<>();

    try {
      for (Path subtopicPath : Files.walk(TOPICS_PUBLIC).filter(p -> p.toString().endsWith(".concepts")).toList())
        Files.lines(subtopicPath)
            .filter(line -> line.contains("="))
            .forEach(line -> {
              String[] arr = line.split("=", 2);
              if (arr[0].contains(";")) throw new RuntimeException("Key '" + arr[0] + "' should not contain semicolons (;)");
              if (arr[0].contains(",")) throw new RuntimeException("Key '" + arr[0] + "' should not contain commas (,)");

              ConceptDTO repeated = key_concept.put(arr[0], new ConceptDTO(arr[0], arr[1]));
              if (repeated != null) {
                SimpleColorPrint.redInLine("The repeated key: ");
                SimpleColorPrint.blueInLine(arr[0]);
                SimpleColorPrint.redInLine(", the definitions: ");
                SimpleColorPrint.blueInLine(arr[1]);
                SimpleColorPrint.redInLine(" and ");
                SimpleColorPrint.blue(repeated.definition);
              }
            });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return key_concept;
  }
}
