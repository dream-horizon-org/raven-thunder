package com.dream11.thunder.api;

import com.dream11.common.app.AbstractApplication;
import com.dream11.common.app.Deployable;
import com.dream11.common.app.VerticleConfig;
import com.dream11.thunder.api.dao.statemachine.StateMachineRepositoryModule;
import com.dream11.thunder.api.service.cache.StaticDataCacheModule;
import com.dream11.thunder.api.service.cohort.UserCohortsModule;
import com.dream11.thunder.api.service.debug.AppDebugModule;
import com.dream11.thunder.api.service.sdk.CtaSdkApiModule;
import com.dream11.thunder.api.verticle.RestVerticle;
import com.dream11.thunder.core.dao.behaviourTag.BehaviourTagRepositoryModule;
import com.dream11.thunder.core.dao.cta.CTARepositoryModule;
import com.dream11.thunder.core.dao.nudge.NudgeRepositoryModule;
import com.dream11.thunder.core.dao.nudge.preview.NudgePreviewRepositoryModule;
import com.dream11.thunder.core.service.web.WebModule;
import com.google.inject.Module;
import io.vertx.reactivex.core.Vertx;

public class MainApplication extends AbstractApplication {

  public static void main(String[] args) {
    MainApplication app = new MainApplication();
    app.rxStartApplication().subscribe();
  }

  @Override
  protected Module[] getGoogleGuiceModules(Vertx vertx) {
    return new Module[] {
      new MainModule(vertx),
      new WebModule(vertx),
      new UserCohortsModule(vertx),
      new CtaSdkApiModule(vertx),
      new AppDebugModule(vertx),
      new StateMachineRepositoryModule(vertx),
      new NudgeRepositoryModule(vertx),
      new NudgePreviewRepositoryModule(vertx),
      new StaticDataCacheModule(vertx),
      new CTARepositoryModule(vertx),
      new BehaviourTagRepositoryModule(vertx)
    };
  }

  @Override
  protected Deployable[] getVerticlesToDeploy(Vertx vertx) {
    return new Deployable[] {
      new Deployable(
          VerticleConfig.builder().instances(getEventLoopSize()).verticleType(0).build(),
          RestVerticle.class)
    };
  }
}
