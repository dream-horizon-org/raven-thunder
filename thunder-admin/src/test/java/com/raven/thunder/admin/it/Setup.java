package com.raven.thunder.admin.it;

import io.restassured.RestAssured;
import io.vertx.rxjava3.core.Vertx;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * Test setup for Thunder Admin: - Starts Aerospike Testcontainer - Configures dynamic HTTP port and
 * RestAssured - Initializes Guice and deploys MainVerticle
 */
public class Setup
    implements ExtensionContext.Store.CloseableResource,
        BeforeAllCallback,
        TestInstancePostProcessor {

  private final Vertx vertx;
  private GenericContainer<?> aerospikeContainer;
  private final int httpPort;

  public Setup() {
    this.httpPort = findFreePort();

    initializeAerospikeContainer();
    initializeRestAssuredConfig();

    this.vertx = Vertx.vertx();
    initializeGuiceInjector();
    overrideServerPortForApp();
    startApplication();
  }

  private void initializeAerospikeContainer() {
    this.aerospikeContainer =
        new GenericContainer<>(DockerImageName.parse("aerospike/aerospike-server:6.4.0.3"))
            .withExposedPorts(3000)
            .waitingFor(Wait.forListeningPort())
            .withReuse(false);
    this.aerospikeContainer.start();
    // Propagate mapped host/port so the app connects to this container
    String host = this.aerospikeContainer.getHost();
    Integer mappedPort = this.aerospikeContainer.getMappedPort(3000);
    System.setProperty("AEROSPIKE_HOST", host);
    System.setProperty("AEROSPIKE_PORT", String.valueOf(mappedPort));
    try {
      Thread.sleep(3000);
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

  private void initializeRestAssuredConfig() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = httpPort;
  }

  private void overrideServerPortForApp() {
    // ConfigUtil reads THUNDER_PORT from system properties/env and overrides server.port
    System.setProperty("THUNDER_PORT", String.valueOf(httpPort));
  }

  private void initializeGuiceInjector() {
    try {
      com.raven.thunder.admin.injection.GuiceInjector.initialize(
          java.util.List.of(new com.raven.thunder.admin.injection.MainModule(vertx.getDelegate())));
    } catch (IllegalStateException ignored) {
      // Already initialized - safe to continue
    }
  }

  private void startApplication() {
    this.vertx.rxDeployVerticle("com.raven.thunder.admin.verticle.MainVerticle").blockingGet();
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("ThunderAdminSetup", this);
  }

  @Override
  public void close() {
    if (this.aerospikeContainer != null) {
      this.aerospikeContainer.stop();
    }
    if (this.vertx != null) {
      this.vertx.close().blockingAwait();
    }
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
    // Reserved for future injections
  }

  private static int findFreePort() {
    try (java.net.ServerSocket socket = new java.net.ServerSocket(0)) {
      socket.setReuseAddress(true);
      return socket.getLocalPort();
    } catch (java.io.IOException e) {
      return 8081;
    }
  }
}
