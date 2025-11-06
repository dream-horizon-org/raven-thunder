package com.dream11.thunder.api.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * Guice injector holder for Thunder API.
 * Provides centralized dependency injection.
 */
@Slf4j
public class GuiceInjector {

    private static Injector injector;

    /**
     * Initialize the Guice injector with Vertx instance.
     *
     * @param vertx Vertx instance
     */
    public static void initialize(Vertx vertx) {
        if (injector == null) {
            log.info("Initializing Guice injector for Thunder API");
            injector = Guice.createInjector(new MainModule(vertx));
            log.info("Guice injector initialized successfully");
        }
    }

    /**
     * Get instance of a class from Guice.
     *
     * @param clazz Class to get instance of
     * @param <T> Type parameter
     * @return Instance of the class
     */
    public static <T> T getInstance(Class<T> clazz) {
        if (injector == null) {
            throw new IllegalStateException("Guice injector not initialized. Call initialize() first.");
        }
        return injector.getInstance(clazz);
    }

    /**
     * Get the Guice injector.
     *
     * @return Guice injector
     */
    public static Injector getInjector() {
        if (injector == null) {
            throw new IllegalStateException("Guice injector not initialized. Call initialize() first.");
        }
        return injector;
    }
}
