package com.dream11.thunder.api.service.sdk;

import com.dream11.thunder.api.dao.StateMachineRepository;
import com.dream11.thunder.api.io.request.CTASnapshotRequest;
import com.dream11.thunder.api.io.response.CTAResponse;
import com.dream11.thunder.api.service.SdkService;
import com.dream11.thunder.api.service.StaticDataCache;
import com.dream11.thunder.api.service.UserCohortsClient;
import com.dream11.thunder.api.util.CTAFilterUtil;
import com.dream11.thunder.api.util.CTASnapshotMerger;
import com.dream11.thunder.api.util.StateMachineUtil;
import com.dream11.thunder.core.dao.NudgePreviewRepository;
import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.NudgePreview;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * SDK service implementation responsible for resolving eligible CTAs for a user, merging client
 * delta snapshots, and persisting user state machine snapshots.
 *
 * <p>It relies on: - UserCohortsClient to fetch user cohorts - StaticDataCache for active/paused
 * CTAs and behaviour tags - StateMachineRepository for reading/upserting user snapshots - Utilities
 * (CTAFilterUtil, StateMachineUtil, CTASnapshotMerger) for clean logic separation
 */
@Slf4j
public class SdkServiceImpl implements SdkService {

  private final UserCohortsClient userCohortsClient;
  private final StateMachineRepository stateMachineRepository;
  private final StaticDataCache cache;
  private final NudgePreviewRepository nudgePreviewRepository;

  private final RuleMapper ruleMapper = new RuleMapper();
  private final BehaviourExposureRuleMapper behaviourExposureRuleMapper =
      new BehaviourExposureRuleMapper();
  private final CTARelationMapper ctaRelationMapper = new CTARelationMapper();
  private final CTASnapshotMerger ctaSnapshotMerger =
      new CTASnapshotMerger(ruleMapper, behaviourExposureRuleMapper, ctaRelationMapper);

  @Inject
  public SdkServiceImpl(
      UserCohortsClient userCohortsClient,
      StateMachineRepository stateMachineRepository,
      StaticDataCache staticDataCache,
      NudgePreviewRepository nudgePreviewRepository) {
    this.userCohortsClient = userCohortsClient;
    this.stateMachineRepository = stateMachineRepository;
    this.cache = staticDataCache;
    this.nudgePreviewRepository = nudgePreviewRepository;
  }

  @Override
  public Maybe<CTAResponse> appLaunch(
      String tenantId, Long userId, CTASnapshotRequest deltaSnapshot) {
    return userCohortsClient
        .findAllCohorts(userId)
        .map(
            cohorts ->
                CTAFilterUtil.filterEligibleCTAs(
                    tenantId,
                    cohorts,
                    CTAFilterUtil.filterByTenant(cache.findAllActiveCTA(), tenantId)))
        .filter(activeCTAs -> !activeCTAs.isEmpty())
        .flatMapSingle(
            activeCTAs ->
                loadOrCreateSnapshot(tenantId, userId)
                    .flatMap(
                        snapshot -> {
                          boolean updated =
                              updateSnapshotWithStaleData(tenantId, activeCTAs, snapshot);
                          updated |= mergeDeltaSnapshotIfPresent(snapshot, deltaSnapshot);

                          if (updated) {
                            return stateMachineRepository
                                .upsert(tenantId, userId, snapshot)
                                .map(ignored -> snapshot);
                          }
                          return Single.just(snapshot);
                        })
                    .map(snapshot -> buildCTAResponse(tenantId, activeCTAs, snapshot)));
  }

  @Override
  public Single<Boolean> merge(String tenantId, Long userId, CTASnapshotRequest deltaSnapshot) {
    return loadOrCreateSnapshot(tenantId, userId)
        .map(
            snapshot -> {
              StateMachineUtil.mergeDeltaSnapshot(snapshot, deltaSnapshot);
              return snapshot;
            })
        .flatMap(snapshot -> stateMachineRepository.upsert(tenantId, userId, snapshot));
  }

  @Override
  public Maybe<NudgePreview> findNudgePreview(String tenantId, String id) {
    return nudgePreviewRepository.find(tenantId, id);
  }

  private Single<com.dream11.thunder.api.model.UserDataSnapshot> loadOrCreateSnapshot(
      String tenantId, Long userId) {
    return stateMachineRepository
        .find(tenantId, userId)
        .switchIfEmpty(
            Single.defer(
                () ->
                    Single.just(
                        new com.dream11.thunder.api.model.UserDataSnapshot(
                            new HashMap<>(), new HashMap<>()))));
  }

  private boolean updateSnapshotWithStaleData(
      String tenantId,
      Map<Long, CTA> activeCTAs,
      com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    Map<Long, CTA> pausedCTAs = CTAFilterUtil.filterByTenant(cache.findAllPausedCTA(), tenantId);
    return StateMachineUtil.archiveStaleData(activeCTAs, pausedCTAs, snapshot);
  }

  private boolean mergeDeltaSnapshotIfPresent(
      com.dream11.thunder.api.model.UserDataSnapshot snapshot, CTASnapshotRequest deltaSnapshot) {
    if (deltaSnapshot != null
        && deltaSnapshot.getCtas() != null
        && !deltaSnapshot.getCtas().isEmpty()) {
      StateMachineUtil.mergeDeltaSnapshot(snapshot, deltaSnapshot);
      return true;
    }
    return false;
  }

  private CTAResponse buildCTAResponse(
      String tenantId,
      Map<Long, CTA> activeCTAs,
      com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    Map<String, BehaviourTag> tenantBehaviourTags =
        cache.findAllBehaviourTags().entrySet().stream()
            .filter(e -> tenantId.equals(e.getValue().getTenantId()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    return ctaSnapshotMerger.mergeCTAWithSnapshot(tenantBehaviourTags, activeCTAs, snapshot);
  }
}
