package com.dream11.thunder.core.util;

import java.util.List;

public class FormatUtil {
  public static List<String> extractList(String stringList) {
    if (stringList == null) return List.of();
    return List.of(stringList.replaceAll("[\\[\"\\]]", "").split(","));
  }
}

