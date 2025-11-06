package com.dream11.thunder.admin.service;

import com.dream11.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.admin.io.response.BehaviourTagsResponse;
import com.dream11.thunder.core.model.BehaviourTag;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface BehaviourTagService {

  Single<BehaviourTagsResponse> fetchAllBehaviourTags(String tenantId);

  Single<BehaviourTag> fetchBehaviourTagDetail(String tenantId, String behaviourTagName);

  Completable createBehaviourTag(
      String tenantId, String user, BehaviourTagCreateRequest behaviourTagCreateRequest);

  Completable updateBehaviourTag(
      String tenantId,
      String behaviourTagName,
      BehaviourTagPutRequest behaviourTagPutRequest,
      String userId);
}

