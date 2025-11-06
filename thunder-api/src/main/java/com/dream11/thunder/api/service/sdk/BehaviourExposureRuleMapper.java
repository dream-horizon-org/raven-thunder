package com.dream11.thunder.api.service.sdk;

import com.dream11.thunder.api.model.BehaviourExposureRule;
import com.dream11.thunder.core.model.ExposureRule;
import io.reactivex.rxjava3.functions.Function;
import java.util.Collections;

public class BehaviourExposureRuleMapper implements Function<ExposureRule, BehaviourExposureRule> {

  @Override
  public BehaviourExposureRule apply(ExposureRule exposureRule) throws Exception {
    return new BehaviourExposureRule(
        exposureRule.getSession(),
        exposureRule.getLifespan(),
        exposureRule.getWindow(),
        Collections.emptyList());
  }
}
