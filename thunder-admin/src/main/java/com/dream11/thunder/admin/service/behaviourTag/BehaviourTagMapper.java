package com.dream11.thunder.admin.service.behaviourTag;

import com.dream11.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.dream11.thunder.core.model.BehaviourTag;

public class BehaviourTagMapper {

  public BehaviourTag apply(String tenantId, BehaviourTagCreateRequest behaviourTag, String user, Long id) {
    return new BehaviourTag(
        id,
        behaviourTag.getBehaviourTagName(),
        behaviourTag.getDescription(),
        System.currentTimeMillis(),
        user,
        0L,
        null,
        behaviourTag.getExposureRule(),
        behaviourTag.getCtaRelation(),
        behaviourTag.getLinkedCtas(),
        tenantId);
  }
}

