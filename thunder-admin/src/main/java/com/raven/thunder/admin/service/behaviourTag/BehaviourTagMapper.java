package com.raven.thunder.admin.service.behaviourTag;

import com.raven.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.raven.thunder.core.model.BehaviourTag;

public class BehaviourTagMapper {

  /**
   * Maps a create request to a BehaviourTag model with createdBy/createdAt set.
   *
   * @param tenantId tenant identifier
   * @param behaviourTag request payload
   * @param user user creating the tag
   * @return BehaviourTag model ready to persist
   */
  public BehaviourTag apply(String tenantId, BehaviourTagCreateRequest behaviourTag, String user) {
    return new BehaviourTag(
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
