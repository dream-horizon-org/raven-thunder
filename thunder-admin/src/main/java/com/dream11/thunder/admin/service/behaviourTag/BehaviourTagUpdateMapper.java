package com.dream11.thunder.admin.service.behaviourTag;

import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.core.model.BehaviourTag;

public class BehaviourTagUpdateMapper {

  /**
   * Maps an update request to a BehaviourTag model, setting lastUpdatedBy/lastUpdatedAt,
   * and preserving the tag name as identifier.
   *
   * @param tenantId tenant identifier
   * @param behaviourTag update request
   * @param behaviourTagName tag name (immutable key)
   * @param userId user performing the update
   * @return updated BehaviourTag model
   */
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

