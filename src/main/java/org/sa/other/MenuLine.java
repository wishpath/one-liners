package org.sa.other;

import org.sa.config.Props;
import org.sa.console.ColoredString;

public class MenuLine {
  public static String string(String command, String function) {
    return
        "\n" +
            Props.TAB + ColoredString.red(command) +
        " - " + function;
  }
}
