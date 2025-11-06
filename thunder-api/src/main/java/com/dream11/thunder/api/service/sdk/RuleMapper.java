package com.dream11.thunder.api.service.sdk;

import com.dream11.thunder.api.io.response.RuleResponse;
import com.dream11.thunder.core.model.rule.Rule;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;

public class RuleMapper implements Function<Rule, RuleResponse> {

  @NonNull
  @Override
  public RuleResponse apply(@NonNull Rule rule) {
    return new RuleResponse(
        rule.getStateToAction(),
        rule.getResetStates(),
        rule.getResetCTAonFirstLaunch(),
        rule.getContextParams(),
        rule.getStateTransition(),
        rule.getGroupByConfig(),
        rule.getPriority(),
        rule.getStateMachineTTL(),
        rule.getCtaValidTill(),
        rule.getActions(),
        rule.getFrequency());
  }
}
