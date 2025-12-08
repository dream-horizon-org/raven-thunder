package com.raven.thunder.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.raven.thunder.core.config.Config;
import io.reactivex.rxjava3.core.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for loading and managing application configuration. Provides type-safe access to
 * configuration through the Config class.
 */
@Slf4j
public final class ConfigUtil {

  private static final ObjectMapper objectMapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .configure(
              com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
              false);

  private static final String DEFAULT_CONFIG_FILE = "thunder-default.conf";
  private static final String LOCAL_OVERRIDE_CONFIG_FILE = "thunder.conf";
  private static final String ADMIN_DEFAULT_CONFIG_FILE = "admin-default.conf";
  private static final String ADMIN_LOCAL_OVERRIDE_CONFIG_FILE = "admin.conf";

  private ConfigUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Loads the application configuration using ConfigRetriever and returns it as a type-safe Config
   * object. Loads from HOCON files and environment variables (env vars override file values). Uses
   * default config file names for API module.
   *
   * @param vertx the Vert.x instance
   * @return a Single that emits the parsed Config object
   */
  public static Single<Config> getConfig(Vertx vertx) {
    return getConfig(vertx, DEFAULT_CONFIG_FILE, LOCAL_OVERRIDE_CONFIG_FILE);
  }

  /**
   * Loads the application configuration using ConfigRetriever and returns it as a type-safe Config
   * object. Loads from HOCON files and environment variables (env vars override file values). Uses
   * admin config file names for Admin module.
   *
   * @param vertx the Vert.x instance
   * @return a Single that emits the parsed Config object
   */
  public static Single<Config> getAdminConfig(Vertx vertx) {
    return getConfig(vertx, ADMIN_DEFAULT_CONFIG_FILE, ADMIN_LOCAL_OVERRIDE_CONFIG_FILE);
  }

  /**
   * Loads the application configuration using ConfigRetriever and returns it as a type-safe Config
   * object. Loads from HOCON files and environment variables (env vars override file values).
   *
   * @param vertx the Vert.x instance
   * @param defaultConfigFile the default config file name
   * @param overrideConfigFile the override config file name (optional)
   * @return a Single that emits the parsed Config object
   */
  public static Single<Config> getConfig(
      Vertx vertx, String defaultConfigFile, String overrideConfigFile) {
    return getConfigRetriever(vertx, defaultConfigFile, overrideConfigFile)
        .rxGetConfig()
        .map(
            jsonConfig -> {
              try {
                // Override aerospike.host/port from env or system properties for tests/containers
                String aerospikeHost =
                    firstNonEmpty(
                        System.getProperty("AEROSPIKE_HOST"), System.getenv("AEROSPIKE_HOST"));
                String aerospikePortStr =
                    firstNonEmpty(
                        System.getProperty("AEROSPIKE_PORT"), System.getenv("AEROSPIKE_PORT"));
                String serverPortStr =
                    firstNonEmpty(
                        System.getProperty("THUNDER_PORT"), System.getenv("THUNDER_PORT"));

                if (aerospikeHost != null || aerospikePortStr != null) {
                  JsonObject aerospike = jsonConfig.getJsonObject("aerospike");
                  if (aerospike == null) {
                    aerospike = new JsonObject();
                    jsonConfig.put("aerospike", aerospike);
                  }
                  if (aerospikeHost != null && !aerospikeHost.isEmpty()) {
                    aerospike.put("host", aerospikeHost);
                    log.info("Overriding aerospike.host from system/env: {}", aerospikeHost);
                  }
                  if (aerospikePortStr != null && !aerospikePortStr.isEmpty()) {
                    try {
                      int port = Integer.parseInt(aerospikePortStr);
                      aerospike.put("port", port);
                      log.info("Overriding aerospike.port from system/env: {}", port);
                    } catch (NumberFormatException nfe) {
                      log.warn("Invalid AEROSPIKE_PORT '{}', ignoring override", aerospikePortStr);
                    }
                  }
                }
                if (serverPortStr != null && !serverPortStr.isEmpty()) {
                  try {
                    int port = Integer.parseInt(serverPortStr);
                    JsonObject server = jsonConfig.getJsonObject("server");
                    if (server == null) {
                      server = new JsonObject();
                      jsonConfig.put("server", server);
                    }
                    server.put("port", port);
                    log.info("Overriding server.port from system/env: {}", port);
                  } catch (NumberFormatException nfe) {
                    log.warn("Invalid THUNDER_PORT '{}', ignoring override", serverPortStr);
                  }
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

  private static String firstNonEmpty(String a, String b) {
    if (a != null && !a.isEmpty()) return a;
    if (b != null && !b.isEmpty()) return b;
    return null;
  }

  /**
   * Returns a ConfigRetriever that loads configuration from HOCON files and environment variables.
   * Loads default config file first, then overlays override config file if it exists. Environment
   * variables override file values (using Vert.x's env variable substitution).
   *
   * @param vertx the Vert.x instance
   * @param defaultConfigFile the default config file name
   * @param overrideConfigFile the override config file name (optional)
   * @return a ConfigRetriever instance
   */
  public static ConfigRetriever getConfigRetriever(
      Vertx vertx, String defaultConfigFile, String overrideConfigFile) {
    ConfigStoreOptions defaultStore =
        new ConfigStoreOptions()
            .setType("file")
            .setFormat("hocon")
            .setConfig(new JsonObject().put("path", defaultConfigFile));

    ConfigStoreOptions localOverrideStore =
        new ConfigStoreOptions()
            .setType("file")
            .setFormat("hocon")
            .setConfig(new JsonObject().put("path", overrideConfigFile))
            .setOptional(true);

    // Environment variables store (takes precedence)
    ConfigStoreOptions envStore = new ConfigStoreOptions().setType("env");

    ConfigRetrieverOptions options =
        new ConfigRetrieverOptions()
            .addStore(defaultStore)
            .addStore(localOverrideStore)
            .addStore(envStore);

    return ConfigRetriever.create(vertx, options);
  }

  /**
   * Returns a ConfigRetriever that loads configuration from HOCON files and environment variables.
   * Loads thunder-default.conf first, then overlays thunder.conf if it exists. Environment
   * variables override file values (using Vert.x's env variable substitution).
   *
   * @param vertx the Vert.x instance
   * @return a ConfigRetriever instance
   */
  public static ConfigRetriever getConfigRetriever(Vertx vertx) {
    return getConfigRetriever(vertx, DEFAULT_CONFIG_FILE, LOCAL_OVERRIDE_CONFIG_FILE);
  }
}
