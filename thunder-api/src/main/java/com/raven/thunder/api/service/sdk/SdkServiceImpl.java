package com.raven.thunder.api.service.sdk;

import com.google.inject.Inject;
import com.raven.thunder.api.dao.StateMachineRepository;
import com.raven.thunder.api.io.request.CTASnapshotRequest;
import com.raven.thunder.api.io.response.CTAResponse;
import com.raven.thunder.api.model.UserDataSnapshot;
import com.raven.thunder.api.service.SdkService;
import com.raven.thunder.api.service.StaticDataCache;
import com.raven.thunder.api.service.UserCohortsClient;
import com.raven.thunder.api.util.CTAFilterUtil;
import com.raven.thunder.api.util.CTASnapshotMerger;
import com.raven.thunder.api.util.StateMachineUtil;
import com.raven.thunder.core.dao.NudgePreviewRepository;
import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.CTAStatus;
import com.raven.thunder.core.model.NudgePreview;
import com.raven.thunder.core.model.TestCTA;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

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
    Map<Long, CTA> tenantActiveCTAs =
        CTAFilterUtil.filterByTenant(cache.findAllActiveCTA(), tenantId);

    return userCohortsClient
        .findAllCohorts(userId)
        .map(
            cohorts ->
                eligibleCTA(
                    tenantId, cohorts != null ? Set.copyOf(cohorts) : Set.of(), tenantActiveCTAs))
        .map(eligibleCTAs -> addTestCTAsToResponse(eligibleCTAs, tenantId, userId))
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

  private Map<Long, CTA> eligibleCTA(
      String tenantId, Set<String> cohorts, Map<Long, CTA> activeCTAs) {
    return CTAFilterUtil.filterEligibleCTAs(tenantId, cohorts, activeCTAs);
  }

  private Map<Long, CTA> addTestCTAsToResponse(
      Map<Long, CTA> activeCtas, String tenantId, Long userId) {
    String cacheKey = tenantId + ":" + userId;
    List<TestCTA> testCTAs = cache.fetchUserTestCtaMap().get(cacheKey);

    if (ObjectUtils.isNotEmpty(testCTAs)) {
      for (TestCTA testCTA : testCTAs) {
        activeCtas.put(testCTA.getId(), testCtaToCTA(testCTA));
      }
    }

    return activeCtas;
  }

  private CTA testCtaToCTA(TestCTA testCTA) {
    return CTA.builder()
        .id(testCTA.getId())
        .rule(testCTA.getRule())
        .tenantId(testCTA.getTenantId())
        .createdAt(testCTA.getCreatedAt())
        .createdBy(testCTA.getCreatedBy())
        .endTime(testCTA.getExpiresAt())
        .startTime(testCTA.getCreatedAt())
        .lastUpdatedAt(testCTA.getCreatedAt())
        .lastUpdatedBy(testCTA.getCreatedBy())
        .ctaStatus(CTAStatus.LIVE)
        .name("")
        .description("")
        .tags(Collections.emptyList())
        .team("")
        .behaviourTags(Collections.emptyList())
        .build();
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

  private Single<UserDataSnapshot> loadOrCreateSnapshot(String tenantId, Long userId) {
    return stateMachineRepository
        .find(tenantId, userId)
        .switchIfEmpty(
            Single.defer(
                () -> Single.just(new UserDataSnapshot(new HashMap<>(), new HashMap<>()))));
  }

  private boolean updateSnapshotWithStaleData(
      String tenantId, Map<Long, CTA> activeCTAs, UserDataSnapshot snapshot) {
    Map<Long, CTA> pausedCTAs = CTAFilterUtil.filterByTenant(cache.findAllPausedCTA(), tenantId);
    return StateMachineUtil.archiveStaleData(activeCTAs, pausedCTAs, snapshot);
  }

  private boolean mergeDeltaSnapshotIfPresent(
      UserDataSnapshot snapshot, CTASnapshotRequest deltaSnapshot) {
    if (deltaSnapshot != null
        && deltaSnapshot.getCtas() != null
        && !deltaSnapshot.getCtas().isEmpty()) {
      StateMachineUtil.mergeDeltaSnapshot(snapshot, deltaSnapshot);
      return true;
    }
    return false;
  }

  private CTAResponse buildCTAResponse(
      String tenantId, Map<Long, CTA> activeCTAs, UserDataSnapshot snapshot) {
    Map<String, BehaviourTag> tenantBehaviourTags =
        cache.findAllBehaviourTags().entrySet().stream()
            .filter(e -> tenantId.equals(e.getValue().getTenantId()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    return ctaSnapshotMerger.mergeCTAWithSnapshot(tenantBehaviourTags, activeCTAs, snapshot);
  }
}
