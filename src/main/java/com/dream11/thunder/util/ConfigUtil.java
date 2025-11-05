package com.dream11.thunder.util;

import com.dream11.thunder.config.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.reactivex.rxjava3.core.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for loading and managing application configuration.
 * Provides type-safe access to configuration through the Config class.
 */
@Slf4j
public final class ConfigUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String DEFAULT_CONFIG_FILE = "thunder-default.conf";
    private static final String LOCAL_OVERRIDE_CONFIG_FILE = "thunder.conf";

    private ConfigUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Loads the application configuration using ConfigRetriever and returns it as a type-safe Config object.
     * Loads from HOCON files and environment variables (env vars override file values).
     *
     * @param vertx the Vert.x instance
     * @return a Single that emits the parsed Config object
     */
    public static Single<Config> getConfig(Vertx vertx) {
        return getConfigRetriever(vertx)
                .rxGetConfig()
                .map(jsonConfig -> {
                    try {
                        // Override aerospike.host from environment variable if present (for Docker)
                        String aerospikeHost = System.getenv("AEROSPIKE_HOST");
                        if (aerospikeHost != null && !aerospikeHost.isEmpty()) {
                            JsonObject aerospike = jsonConfig.getJsonObject("aerospike");
                            if (aerospike == null) {
                                aerospike = new JsonObject();
                                jsonConfig.put("aerospike", aerospike);
                            }
                            aerospike.put("host", aerospikeHost);
                            log.info("Overriding aerospike.host with environment variable: {}", aerospikeHost);
                        }
                        
                        return objectMapper.readValue(jsonConfig.encode(), Config.class);
                    } catch (Exception e) {
                        log.error("Failed to parse configuration", e);
                        throw new RuntimeException("Failed to parse configuration", e);
                    }
                })
                .doOnSuccess(config -> log.info("Configuration loaded successfully"))
                .doOnError(error -> log.error("Failed to load configuration", error));
    }

    /**
     * Returns a ConfigRetriever that loads configuration from HOCON files and environment variables.
     * Loads thunder-default.conf first, then overlays thunder.conf if it exists.
     * Environment variables override file values (using Vert.x's env variable substitution).
     *
     * @param vertx the Vert.x instance
     * @return a ConfigRetriever instance
     */
    public static ConfigRetriever getConfigRetriever(Vertx vertx) {
        ConfigStoreOptions defaultStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("hocon")
                .setConfig(new JsonObject().put("path", DEFAULT_CONFIG_FILE));

        ConfigStoreOptions localOverrideStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("hocon")
                .setConfig(new JsonObject().put("path", LOCAL_OVERRIDE_CONFIG_FILE))
                .setOptional(true);

        // Environment variables store (takes precedence)
        ConfigStoreOptions envStore = new ConfigStoreOptions()
                .setType("env");

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(defaultStore)
                .addStore(localOverrideStore)
                .addStore(envStore);

        return ConfigRetriever.create(vertx, options);
    }
}

