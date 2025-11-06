package com.dream11.thunder.api.dao.statemachine;

import com.dream11.common.guice.VertxAbstractModule;
import com.dream11.thunder.api.dao.StateMachineRepository;
import io.vertx.reactivex.core.Vertx;
import com.google.inject.Singleton;

public class StateMachineRepositoryModule extends VertxAbstractModule {

  private final Vertx vertx;

  public StateMachineRepositoryModule(Vertx vertx) {
    super(vertx);
    this.vertx = vertx;
  }

  @Override
  protected void bindConfiguration() {
    bind(StateMachineRepository.class).to(StateMachineRepositoryImpl.class).in(Singleton.class);
  }
}
