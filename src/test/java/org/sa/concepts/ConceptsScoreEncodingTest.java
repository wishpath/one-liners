package org.sa.concepts;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConceptsScoreEncodingTest {

  @Test
  void loadScoresWithEmAndEnDash() throws IOException {
    Path tempFile = Files.createTempFile("score", ".properties");
    String originalKeyEm = "em dash (—) function";
    String originalKeyEn = "en dash (–) function";

    // write with UTF-8 (escaping spaces so Properties can read correctly)
    try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
      writer.write(originalKeyEm.replace(" ", "\\ ") + "=1\n");
      writer.write(originalKeyEn.replace(" ", "\\ ") + "=2\n");
    }

    // 1) Load with InputStream (ISO-8859-1) → corrupt
    Properties propsIso = new Properties();
    try (InputStream in = Files.newInputStream(tempFile)) {
      propsIso.load(in);
    }
    assertFalse(propsIso.containsKey(originalKeyEm));
    assertFalse(propsIso.containsKey(originalKeyEn));

    // 2) Load with Reader (UTF-8) → correct
    Properties propsUtf8 = new Properties();
    try (Reader reader = Files.newBufferedReader(tempFile, StandardCharsets.UTF_8)) {
      propsUtf8.load(reader);
    }

    assertEquals("1", propsUtf8.getProperty(originalKeyEm));
    assertEquals("2", propsUtf8.getProperty(originalKeyEn));
  }
}
