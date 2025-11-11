package com.dream11.thunder.admin.service.behaviourTag;

import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.core.model.BehaviourTag;

public class BehaviourTagUpdateMapper {

  public BehaviourTag apply(
      String tenantId, BehaviourTagPutRequest behaviourTag, BehaviourTag existingBehaviourTag, String userId) {
    return new BehaviourTag(
        existingBehaviourTag.getId(),
        existingBehaviourTag.getName(),
        behaviourTag.getDescription(),
        existingBehaviourTag.getCreatedAt(),
        existingBehaviourTag.getCreatedBy(),
        System.currentTimeMillis(),
        userId,
        behaviourTag.getExposureRule(),
        behaviourTag.getCtaRelation(),
        behaviourTag.getLinkedCtas(),
        tenantId);
  }
}

