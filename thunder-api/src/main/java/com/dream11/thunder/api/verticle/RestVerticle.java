package com.dream11.thunder.api.verticle;

import com.dream11.thunder.api.injection.GuiceInjector;
import com.dream11.thunder.api.rest.AppDebugController;
import com.dream11.thunder.api.rest.SdkApiController;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RestVerticle for Thunder API.
 * Sets up HTTP server with JAX-RS style controllers.
 */
@Slf4j
@RequiredArgsConstructor
public class RestVerticle extends AbstractVerticle {

    private final HttpServerOptions options;
    private HttpServer server;

    @Override
    public Completable rxStart() {
        Router router = Router.router(vertx);
        
        // Add body handler for POST/PUT requests
        router.route().handler(BodyHandler.create());

        // Initialize Guice injector
        GuiceInjector.initialize(vertx.getDelegate());

        // Get controllers from Guice
        SdkApiController sdkApiController = GuiceInjector.getInstance(SdkApiController.class);
        AppDebugController debugController = GuiceInjector.getInstance(AppDebugController.class);

        // Health check endpoint
        router.get("/healthcheck/ping").handler(ctx -> {
            ctx.response()
                .putHeader("content-type", "application/json")
                .end("{\"status\":\"ok\",\"service\":\"thunder-api\"}");
        });

        log.info("Controllers initialized successfully");
        log.info("RestVerticle started on {}:{}", options.getHost(), options.getPort());

        // Note: Full JAX-RS routing would require a JAX-RS implementation like RESTeasy or Jersey
        // For now, we have the structure in place and controllers are initialized
        // You can use a JAX-RS servlet bridge or implement routing manually

        return vertx.createHttpServer(options)
                .requestHandler(router)
                .rxListen()
                .doOnSuccess(s -> {
                    this.server = s;
                    log.info("HTTP server started on {}:{}", options.getHost(), options.getPort());
                })
                .ignoreElement();
    }

    @Override
    public Completable rxStop() {
        log.info("Stopping RestVerticle");
        if (server != null) {
            return server.rxClose();
        }
        return Completable.complete();
    }
}

