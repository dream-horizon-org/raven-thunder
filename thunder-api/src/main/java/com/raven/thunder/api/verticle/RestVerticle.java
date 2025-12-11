package com.raven.thunder.api.verticle;

import com.dream11.rest.AbstractRestVerticle;
import com.dream11.rest.ClassInjector;
import com.raven.thunder.api.injection.GuiceInjector;
import io.vertx.core.http.HttpServerOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestVerticle extends AbstractRestVerticle {

  private static final String PACKAGE_NAME = "com.raven.thunder.api";

  public RestVerticle() {
    super(PACKAGE_NAME);
  }

  public RestVerticle(HttpServerOptions options) {
    super(PACKAGE_NAME, options);
  }

  @Override
  protected ClassInjector getInjector() {
    return GuiceInjector.getGuiceInjector();
  }

  @Override
  public void stop() throws Exception {
    log.info("Stopping RestVerticle");
    super.stop();
  }
}
