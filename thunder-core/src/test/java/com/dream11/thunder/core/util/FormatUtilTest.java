package com.dream11.thunder.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class FormatUtilTest {

  @Test
  void extractList_parsesBracketedCsv() {
    List<String> out = FormatUtil.extractList("[a,b,c]");
    assertThat(out).containsExactly("a", "b", "c");
  }

  @Test
  void extractList_parsesPlainCsv() {
    List<String> out = FormatUtil.extractList("a,b");
    assertThat(out).containsExactly("a", "b");
  }

  @Test
  void extractList_handlesQuotesAndNull() {
    List<String> out = FormatUtil.extractList("[\"a\",\"b\"]");
    assertThat(out).containsExactly("a", "b");
    assertThat(FormatUtil.extractList(null)).isEmpty();
  }
}
