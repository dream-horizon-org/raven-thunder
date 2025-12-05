package com.dream11.thunder.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ParseUtilTest {

  @Test
  void parse_stringToMap_andBack() throws JsonProcessingException {
    String json = "{\"k\":\"v\",\"n\":1}";
    Map<String, Object> map = ParseUtil.parse(json, new TypeReference<Map<String, Object>>() {});
    assertThat(map).containsEntry("k", "v").containsEntry("n", 1);

    String back = ParseUtil.writeValueAsString(map);
    assertThat(back).contains("\"k\"").contains("\"n\"");
  }

  @Test
  void parse_jsonNodeToPojo() throws JsonProcessingException {
    String json = "{\"x\":42}";
    JsonNode node = ParseUtil.parse(json);
    Dummy d = ParseUtil.parse(node, Dummy.class);
    assertThat(d.getX()).isEqualTo(42);
  }

  public static class Dummy {
    private int x;

    public int getX() {
      return x;
    }

    public void setX(int x) {
      this.x = x;
    }
  }
}
