package com.dream11.thunder.util;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SharedDataUtils {

    private static final String DEFAULT_KEY = "default";

    public static <T> void put(Vertx vertx, T value) {
        put(vertx, value, DEFAULT_KEY);
    }

    public static <T> void put(Vertx vertx, T value, String key) {
        SharedData sharedData = vertx.sharedData();
        sharedData.getLocalMap("appData").put(key, value);
        log.debug("Stored value in SharedData with key: {}", key);
    }

    public static <T> T get(Vertx vertx, Class<T> clazz) {
        return get(vertx, clazz, DEFAULT_KEY);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Vertx vertx, Class<T> clazz, String key) {
        SharedData sharedData = vertx.sharedData();
        Object value = sharedData.getLocalMap("appData").get(key);
        if (value == null) {
            log.warn("No value found in SharedData for key: {}", key);
            return null;
        }
        if (!clazz.isInstance(value)) {
            log.error("Value for key {} is not an instance of {}", key, clazz.getName());
            throw new ClassCastException("Value is not an instance of " + clazz.getName());
        }
        return (T) value;
    }
}

