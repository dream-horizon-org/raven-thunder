package com.dream11.thunder.injection;

import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;

public class MainModule extends AbstractModule {

    private final Vertx vertx;

    public MainModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    protected void configure() {
        bind(Vertx.class).toInstance(vertx);
        bind(io.vertx.rxjava3.core.Vertx.class)
                .toInstance(io.vertx.rxjava3.core.Vertx.newInstance(vertx));
    }
}

