package org.sa.other;

import org.sa.console.Colors;

public class MenuLine {
  static String newLine (String command, String function) {
    return
        "\n" +
        Colors.RED + command + Colors.RESET +
        " - " + function +
        "\n";
  }
}
