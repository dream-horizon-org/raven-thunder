package com.dream11.thunder.admin.service.behaviourTag;

import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.core.model.BehaviourTag;

public class BehaviourTagUpdateMapper {

  public BehaviourTag apply(
      String tenantId, BehaviourTagPutRequest behaviourTag, String behaviourTagName, String userId) {
    return new BehaviourTag(
        behaviourTagName,
        behaviourTag.getDescription(),
        0L,
        null,
        System.currentTimeMillis(),
        userId,
        behaviourTag.getExposureRule(),
        behaviourTag.getCtaRelation(),
        behaviourTag.getLinkedCtas(),
        tenantId);
  }
}

