package com.dream11.thunder.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;

/** JSON parsing utilities based on Jackson ObjectMapper. */
public class ParseUtil {

  private static final ObjectMapper mapper =
      new ObjectMapper().registerModule(new JavaTimeModule());

  /** Parses a JsonNode into a concrete type. */
  public static <T> T parse(JsonNode content, Class<T> typeClazz) throws JsonProcessingException {
    return mapper.treeToValue(content, typeClazz);
  }

  /** Converts a map into a concrete type. */
  public static <T> T parse(Map<String, Object> content, Class<T> typeClazz) {
    return mapper.convertValue(content, typeClazz);
  }

  /** Converts a map into a parameterized type using a TypeReference. */
  public static <T> T parse(Map<String, Object> content, TypeReference<T> typeRef) {
    return mapper.convertValue(content, typeRef);
  }

  /** Parses a JSON string into a concrete type. */
  public static <T> T parse(String content, Class<T> typeClazz) throws JsonProcessingException {
    return mapper.readValue(content, typeClazz);
  }

  /** Parses a JSON string into a parameterized type using a TypeReference. */
  public static <T> T parse(String content, TypeReference<T> typeRef)
      throws JsonProcessingException {
    return mapper.readValue(content, typeRef);
  }

  /** Parses a JSON string into a JsonNode. */
  public static JsonNode parse(String content) throws JsonProcessingException {
    return mapper.readTree(content);
  }

  /** Serializes an object into a JSON string. */
  public static String writeValueAsString(Object object) throws JsonProcessingException {
    return mapper.writeValueAsString(object);
  }
}
