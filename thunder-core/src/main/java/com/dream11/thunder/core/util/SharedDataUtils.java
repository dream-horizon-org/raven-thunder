package com.dream11.thunder.core.util;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Shareable;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for managing shared data across Vert.x instances. Provides thread-safe singleton
 * management with support for named instances.
 */
@Slf4j
public final class SharedDataUtils {

  private static final String SHARED_DATA_MAP_NAME = "appData";
  private static final String DEFAULT_KEY = "default";

  private SharedDataUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /** Stores an instance in shared data with default key. */
  public static <T> void put(Vertx vertx, T instance) {
    put(vertx, instance, DEFAULT_KEY);
  }

  /** Stores an instance in shared data with a specific key. */
  public static <T> void put(Vertx vertx, T instance, String key) {
    Objects.requireNonNull(vertx, "Vertx cannot be null");
    Objects.requireNonNull(instance, "Instance cannot be null");
    Objects.requireNonNull(key, "Key cannot be null");

    LocalMap<String, ThreadSafe<T>> sharedDataMap =
        vertx.sharedData().getLocalMap(SHARED_DATA_MAP_NAME);
    sharedDataMap.put(key, new ThreadSafe<>(instance));
    log.debug("Stored value in SharedData with key: {}", key);
  }

  /** Retrieves an instance from shared data using default key. */
  public static <T> T get(Vertx vertx, Class<T> clazz) {
    return get(vertx, clazz, DEFAULT_KEY);
  }

  /** Retrieves an instance from shared data using a specific key. */
  @SuppressWarnings("unchecked")
  public static <T> T get(Vertx vertx, Class<T> clazz, String key) {
    Objects.requireNonNull(vertx, "Vertx cannot be null");
    Objects.requireNonNull(clazz, "Class cannot be null");
    Objects.requireNonNull(key, "Key cannot be null");

    LocalMap<String, ThreadSafe<T>> sharedDataMap =
        vertx.sharedData().getLocalMap(SHARED_DATA_MAP_NAME);
    ThreadSafe<T> wrapper = sharedDataMap.get(key);
    if (wrapper == null) {
      log.warn("No value found in SharedData for key: {}", key);
      return null;
    }
    T value = wrapper.object();
    if (!clazz.isInstance(value)) {
      log.error("Value for key {} is not an instance of {}", key, clazz.getName());
      throw new ClassCastException("Value is not an instance of " + clazz.getName());
    }
    return value;
  }

  /**
   * Thread-safe wrapper for shared objects. Implements Shareable to ensure proper serialization in
   * Vert.x.
   */
  record ThreadSafe<T>(@Getter T object) implements Shareable {
    public ThreadSafe {
      Objects.requireNonNull(object, "Object cannot be null");
    }
  }
}
