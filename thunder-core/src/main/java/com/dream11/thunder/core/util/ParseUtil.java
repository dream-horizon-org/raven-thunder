package com.dream11.thunder.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;

public class ParseUtil {

  private static final ObjectMapper mapper = new ObjectMapper()
          .registerModule(new JavaTimeModule());

  public static <T> T parse(JsonNode content, Class<T> typeClazz) throws JsonProcessingException {
    return mapper.treeToValue(content, typeClazz);
  }

  public static <T> T parse(Map<String, Object> content, Class<T> typeClazz) {
    return mapper.convertValue(content, typeClazz);
  }

  public static <T> T parse(Map<String, Object> content, TypeReference<T> typeRef) {
    return mapper.convertValue(content, typeRef);
  }

  public static <T> T parse(String content, Class<T> typeClazz) throws JsonProcessingException {
    return mapper.readValue(content, typeClazz);
  }

  public static <T> T parse(String content, TypeReference<T> typeRef)
      throws JsonProcessingException {
    return mapper.readValue(content, typeRef);
  }

  public static JsonNode parse(String content) throws JsonProcessingException {
    return mapper.readTree(content);
  }

  public static String writeValueAsString(Object object) throws JsonProcessingException {
    return mapper.writeValueAsString(object);
  }
}

