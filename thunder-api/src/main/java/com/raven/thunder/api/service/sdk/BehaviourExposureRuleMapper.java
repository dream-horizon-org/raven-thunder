package com.raven.thunder.api.service.sdk;

import com.raven.thunder.api.model.BehaviourExposureRule;
import com.raven.thunder.core.model.ExposureRule;
import io.reactivex.rxjava3.functions.Function;
import java.util.Collections;

/** Maps core ExposureRule to API BehaviourExposureRule used in SDK responses. */
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
