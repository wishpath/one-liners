package org.sa.util;

import org.sa.console.SimpleColorPrint;

public class StringConsoleUtil {
  public static void printStringWithFragmentHighlighted(String fragment, String string, String mainColorAnsiCode, String fragmentColorAnsiCode) {
    //lowercase strings for easy finding of index where the fragment is
    String lowerS = string.toLowerCase();
    String lowerFragment = fragment.toLowerCase();

    //each iteration will print up to the end of next matching fragment
    for (int indexOfPrintStart = 0; indexOfPrintStart < string.length(); ) {
      int indexOfFragment = lowerS.indexOf(lowerFragment, indexOfPrintStart);
      if (indexOfFragment == -1) {
        SimpleColorPrint.color(string.substring(indexOfPrintStart), mainColorAnsiCode);
        break;
      }
      //print before fragment
      SimpleColorPrint.colorInLine(string.substring(indexOfPrintStart, indexOfFragment), mainColorAnsiCode);
      //print fragment
      SimpleColorPrint.colorInLine(string.substring(indexOfFragment, indexOfFragment + fragment.length()), fragmentColorAnsiCode);
      indexOfPrintStart = indexOfFragment + fragment.length();
    }
  }
}
