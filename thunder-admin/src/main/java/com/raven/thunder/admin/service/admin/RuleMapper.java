package com.raven.thunder.admin.service.admin;

import com.raven.thunder.admin.io.request.RuleRequest;
import com.raven.thunder.core.model.rule.Rule;

public class RuleMapper {

  /**
   * Maps a RuleRequest and CTA validity to a Rule domain object.
   *
   * @param ruleRequest incoming rule definition
   * @param ctaValidTill optional end time used in rule
   * @return Rule model
   */
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
