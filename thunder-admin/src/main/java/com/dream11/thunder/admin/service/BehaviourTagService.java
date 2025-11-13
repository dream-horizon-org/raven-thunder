package com.dream11.thunder.admin.service;

import com.dream11.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.admin.io.response.BehaviourTagsResponse;
import com.dream11.thunder.core.model.BehaviourTag;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface BehaviourTagService {

  /**
   * Lists all behaviour tags for a tenant.
   */
  Single<BehaviourTagsResponse> fetchAllBehaviourTags(String tenantId);

  /**
   * Fetches behaviour tag details by name for a tenant.
   */
  Single<BehaviourTag> fetchBehaviourTagDetail(String tenantId, String behaviourTagName);

  /**
   * Creates a behaviour tag and links eligible CTAs (only DRAFT/PAUSED).
   *
   * @param tenantId tenant identifier
   * @param user user performing the operation
   * @param behaviourTagCreateRequest create request
   */
  Completable createBehaviourTag(
      String tenantId, String user, BehaviourTagCreateRequest behaviourTagCreateRequest);

  /**
   * Updates a behaviour tag and reconciles CTA links (unlink removed, link new ones).
   *
   * @param tenantId tenant identifier
   * @param behaviourTagName behaviour tag name (immutable identifier)
   * @param behaviourTagPutRequest update request
   * @param userId user performing the operation
   */
  Completable updateBehaviourTag(
      String tenantId,
      String behaviourTagName,
      BehaviourTagPutRequest behaviourTagPutRequest,
      String userId);
}

