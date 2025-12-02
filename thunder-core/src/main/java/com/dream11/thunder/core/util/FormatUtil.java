package com.dream11.thunder.core.util;

import java.util.List;

public class FormatUtil {
  /**
   * Parses a string list like "[a,b]" or "a,b" into a List<String>.
   * Returns empty list for null input.
   */
  public static List<String> extractList(String stringList) {
    if (stringList == null) return List.of();
    return List.of(stringList.replaceAll("[\\[\"\\]]", "").split(","));
  }
}

