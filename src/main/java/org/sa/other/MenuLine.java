package org.sa.other;

import org.sa.console.ColoredString;

public class MenuLine {
  public static String string(String command, String function) {
    return
        "\n" +
        ColoredString.red(command) +
        " - " + function;
  }
}
