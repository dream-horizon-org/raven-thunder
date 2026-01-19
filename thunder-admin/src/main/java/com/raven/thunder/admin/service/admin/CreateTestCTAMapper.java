package com.raven.thunder.admin.service.admin;

import com.raven.thunder.admin.io.request.TestCTARequest;
import com.raven.thunder.core.model.TestCTA;
import io.reactivex.rxjava3.annotations.NonNull;
import java.util.concurrent.TimeUnit;

public class CreateTestCTAMapper {

  RuleMapper ruleMapper = new RuleMapper();

  @NonNull
  public TestCTA apply(
      String tenantId,
      @NonNull TestCTARequest ctaRequest,
      @NonNull String user,
      @NonNull Long ctaId) {
    return new TestCTA(
        ctaId,
        ruleMapper.apply(
            ctaRequest.getRule(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)),
        System.currentTimeMillis(),
        System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(ctaRequest.getExpiresInMinutes()),
        ctaRequest.getExpiresInMinutes(),
        user,
        tenantId,
        ctaRequest.getUserIds());
  }
}
