package com.dream11.thunder.admin.service.admin;

import com.dream11.thunder.admin.io.request.RuleRequest;
import com.dream11.thunder.core.model.rule.Rule;

public class RuleMapper {

  public Rule apply(RuleRequest ruleRequest, Long ctaValidTill) {
    return new Rule(
        ruleRequest.getCohortEligibility(),
        ruleRequest.getStateToAction(),
        ruleRequest.getResetStates(),
        ruleRequest.getResetCTAonFirstLaunch(),
        ruleRequest.getContextParams(),
        ruleRequest.getStateTransition(),
        ruleRequest.getGroupByConfig(),
        ruleRequest.getPriority(),
        ruleRequest.getStateMachineTTL(),
        ctaValidTill,
        ruleRequest.getActions(),
        ruleRequest.getFrequency());
  }
}

