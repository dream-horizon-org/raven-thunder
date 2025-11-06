package com.dream11.thunder.api.service.sdk;

import com.dream11.thunder.api.dao.StateMachineRepository;
import com.dream11.thunder.api.io.request.CTASnapshotRequest;
import com.dream11.thunder.api.io.response.CTAResponse;
import com.dream11.thunder.api.io.response.UserCTAAndStateMachineResponse;
import com.dream11.thunder.api.model.BehaviourExposureRule;
import com.dream11.thunder.api.model.BehaviourTagSnapshot;
import com.dream11.thunder.api.model.CTARelationSnapshot;
import com.dream11.thunder.api.model.StateMachine;
import com.dream11.thunder.api.model.StateMachineSnapshot;
import com.dream11.thunder.api.model.UserDataSnapshot;
import com.dream11.thunder.api.service.SdkService;
import com.dream11.thunder.api.service.StaticDataCache;
import com.dream11.thunder.api.service.UserCohortsClient;
import com.dream11.thunder.core.dao.NudgePreviewRepository;
import com.dream11.thunder.core.model.*;
import com.dream11.thunder.core.model.rule.LifespanFrequency;
import com.dream11.thunder.core.model.rule.SessionFrequency;
import com.dream11.thunder.core.model.rule.WindowFrequency;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
public class SdkServiceImpl implements SdkService {

  private final UserCohortsClient userCohortsClient;
  private final StateMachineRepository stateMachineRepository;
  private final StaticDataCache cache;

  private final RuleMapper ruleMapper = new RuleMapper();
  private final BehaviourExposureRuleMapper behaviourExposureRuleMapper =
      new BehaviourExposureRuleMapper();
  private final CTARelationMapper ctaRelationMapper = new CTARelationMapper();
  private final NudgePreviewRepository nudgePreviewRepository;

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
  @Deprecated
  public Maybe<Nudge> findNudge(String id) {
    return Maybe.empty();
  }

  @Override
  public Maybe<CTAResponse> appLaunch(
      String tenantId, Long userId, CTASnapshotRequest deltaSnapshot) {
    return userCohortsClient
        .findAllCohorts(userId)
        .map(
            cohorts ->
                this.eligibleCTA(
                    tenantId,
                    cohorts,
                    cache.findAllActiveCTA().entrySet().stream()
                        .filter(e -> Objects.equals(tenantId, e.getValue().getTenantId()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
        .filter(activeCTAs -> !activeCTAs.isEmpty())
        .flatMapSingle(
            activeCTAs ->
                stateMachineRepository
                    .find(tenantId, userId)
                    .switchIfEmpty(
                        Single.defer(
                            () ->
                                Single.just(
                                    new UserDataSnapshot(new HashMap<>(), new HashMap<>()))))
                    .map(
                        snapshot ->
                            Pair.of(
                                snapshot,
                                archiveStaleData(
                                    activeCTAs,
                                    cache.findAllPausedCTA().entrySet().stream()
                                        .filter(
                                            e ->
                                                Objects.equals(
                                                    tenantId, e.getValue().getTenantId()))
                                        .collect(
                                            Collectors.toMap(
                                                Map.Entry::getKey, Map.Entry::getValue)),
                                    snapshot)))
                    .flatMap(
                        pair -> {
                          final UserDataSnapshot snapshot = pair.getLeft();
                          boolean update = pair.getRight();
                          if (deltaSnapshot != null
                              && deltaSnapshot.getCtas() != null
                              && !deltaSnapshot.getCtas().isEmpty()) {
                            mergeDeltaSnapshot(snapshot, deltaSnapshot);
                            update = true;
                          }
                          if (update) {
                            return stateMachineRepository
                                .upsert(tenantId, userId, snapshot)
                                .map(ignored -> snapshot);
                          }
                          return Single.just(snapshot);
                        })
                    .map(
                        snapshot -> {
                          // filter behaviour tags by tenantId, keep as Map<String, BehaviourTag>
                          Map<String, BehaviourTag> tenantBehaviourTags =
                              cache.findAllBehaviourTags().entrySet().stream()
                                  .filter(e -> Objects.equals(tenantId, e.getValue().getTenantId()))
                                  .collect(
                                      Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                          return mergeCTAWithSnapshot(tenantBehaviourTags, activeCTAs, snapshot);
                        }));
  }

  @Override
  public Single<Boolean> merge(String tenantId, Long userId, CTASnapshotRequest deltaSnapshot) {
    return stateMachineRepository
        .find(tenantId, userId)
        .switchIfEmpty(
            Single.defer(() -> Single.just(new UserDataSnapshot(new HashMap<>(), new HashMap<>()))))
        .map(
            snapshot -> {
              mergeDeltaSnapshot(snapshot, deltaSnapshot);
              return snapshot;
            })
        .flatMap(snapshot -> stateMachineRepository.upsert(tenantId, userId, snapshot));
  }

  @Override
  public Maybe<NudgePreview> findNudgePreview(String tenantId, String id) {
    return nudgePreviewRepository.find(tenantId, id);
  }

  private Map<Long, CTA> eligibleCTA(
      String tenantId, Set<String> cohorts, Map<Long, CTA> activeCTAs) {
    return activeCTAs.entrySet().stream()
        .filter(
            e ->
                tenantId.equals(
                    e.getValue().getTenantId())) // safety net: drop CTAs from other tenants
        .filter(
            cta -> {
              for (String cohort : cta.getValue().getRule().getCohortEligibility().getIncludes()) {
                if (cohorts.contains(cohort)) {
                  return true;
                }
              }
              return false;
            })
        .filter(
            cta -> {
              for (String cohort : cta.getValue().getRule().getCohortEligibility().getExcludes()) {
                if (cohorts.contains(cohort)) {
                  return false;
                }
              }
              return true;
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private boolean archiveStaleData(
      Map<Long, CTA> activeCTAs, Map<Long, CTA> pausedCTAs, UserDataSnapshot snapshot) {
    boolean update = false;

    if (snapshot.getStateMachines() != null) {
      Map<Long, StateMachineSnapshot> stateMachineSnapshot = snapshot.getStateMachines();

      Set<String> activeBehaviourTags = new HashSet<>();

      List<Long> ctaIds = new ArrayList<>(stateMachineSnapshot.keySet());
      for (Long ctaId : ctaIds) {
        if (!(activeCTAs.containsKey(ctaId) || pausedCTAs.containsKey(ctaId))) {
          stateMachineSnapshot.remove(ctaId);
          update = true;
          continue;
        }

        if (activeCTAs.get(ctaId) != null) {
          activeBehaviourTags.addAll(activeCTAs.get(ctaId).getBehaviourTags());
          if (activeCTAs.get(ctaId).getRule().getStateMachineTTL() != null) {
            List<String> groupIds =
                new ArrayList<>(stateMachineSnapshot.get(ctaId).getActiveStateMachines().keySet());
            for (String groupId : groupIds) {
              if ((System.currentTimeMillis()
                      - stateMachineSnapshot
                          .get(ctaId)
                          .getActiveStateMachines()
                          .get(groupId)
                          .getCreatedAt())
                  > activeCTAs.get(ctaId).getRule().getStateMachineTTL()) {
                stateMachineSnapshot.get(ctaId).getActiveStateMachines().remove(groupId);
                update = true;
              }
            }
          }
        } else {
          activeBehaviourTags.addAll(pausedCTAs.get(ctaId).getBehaviourTags());
        }
      }
      List<String> removeTags = new ArrayList<>(Collections.emptyList());
      Set<String> existingBehaviourTags = snapshot.getBehaviourTags().keySet();
      for (String tag : existingBehaviourTags) {
        if (!activeBehaviourTags.contains(tag)) {
          removeTags.add(tag);
        }
      }
      removeTags.forEach(it -> snapshot.getBehaviourTags().remove(it));
    }

    return update;
  }

  private void mergeDeltaSnapshot(UserDataSnapshot snapshot, CTASnapshotRequest deltaSnapshot) {
    if (snapshot.getStateMachines() == null) {
      snapshot.setStateMachines(new HashMap<>());
    }

    if (snapshot.getBehaviourTags() == null) {
      snapshot.setBehaviourTags(new HashMap<>());
    }

    for (StateMachineSnapshot ctaDelta : deltaSnapshot.getCtas()) {
      if (!snapshot.getStateMachines().containsKey(Long.parseLong(ctaDelta.getCtaId()))) {
        snapshot.getStateMachines().put(Long.parseLong(ctaDelta.getCtaId()), ctaDelta);
        continue;
      }

      Map<String, StateMachine> existingSMs =
          snapshot
              .getStateMachines()
              .get(Long.parseLong(ctaDelta.getCtaId()))
              .getActiveStateMachines();

      for (Map.Entry<String, StateMachine> smDelta : ctaDelta.getActiveStateMachines().entrySet()) {
        if (!existingSMs.containsKey(smDelta.getKey())) {
          existingSMs.put(smDelta.getKey(), smDelta.getValue());
          continue;
        }
        if (existingSMs.get(smDelta.getKey()).getLastTransitionAt()
            <= smDelta.getValue().getLastTransitionAt()) {
          existingSMs.put(smDelta.getKey(), smDelta.getValue());
        }
      }

      snapshot
          .getStateMachines()
          .get(Long.parseLong(ctaDelta.getCtaId()))
          .setResetAt(ctaDelta.getResetAt());
      snapshot
          .getStateMachines()
          .get(Long.parseLong(ctaDelta.getCtaId()))
          .setActionDoneAt(ctaDelta.getActionDoneAt()); // TODO : check this
    }

    resetStateMachine(snapshot.getStateMachines(), deltaSnapshot.getCtas());

    if (deltaSnapshot.getBehaviourTags() != null) {
      if (snapshot.getBehaviourTags() == null) {
        snapshot.setBehaviourTags(new HashMap<>());
      }

      for (BehaviourTagSnapshot behaviourTagSnapshot : deltaSnapshot.getBehaviourTags()) {
        snapshot
            .getBehaviourTags()
            .put(behaviourTagSnapshot.getBehaviourTagName(), behaviourTagSnapshot);
      }
    }
  }

  private void resetStateMachine(
      Map<Long, StateMachineSnapshot> snapshot, List<StateMachineSnapshot> deltaSnapshot) {
    for (StateMachineSnapshot ctaDelta : deltaSnapshot) {
      for (Map.Entry<String, StateMachine> smDelta : ctaDelta.getActiveStateMachines().entrySet()) {
        String groupId = smDelta.getKey();
        StateMachine stateMachine = smDelta.getValue();
        Map<String, StateMachine> existingSMs =
            snapshot.get(Long.parseLong(ctaDelta.getCtaId())).getActiveStateMachines();
        if (existingSMs.containsKey(groupId)
            && (existingSMs.get(groupId).getLastTransitionAt()
                <= stateMachine.getLastTransitionAt())
            && (stateMachine.getReset() != null && stateMachine.getReset())) {
          snapshot
              .get(Long.parseLong(ctaDelta.getCtaId()))
              .getActiveStateMachines()
              .remove(groupId);
        }
      }
    }
  }

  // FIXME
  private CTAResponse mergeCTAWithSnapshot(
      Map<String, BehaviourTag> behaviourTagMap,
      Map<Long, CTA> activeCTAs,
      UserDataSnapshot snapshot) {

    List<UserCTAAndStateMachineResponse> userCTAAndStateMachineList = new ArrayList<>();
    List<BehaviourTagSnapshot> behaviourTagSnapshots = new ArrayList<>();
    Set<String> behaviourTagNames = new HashSet<>();

    activeCTAs
        .entrySet()
        .forEach(
            it -> {
              CTA v = it.getValue();
              String behaviourTagName = "";
              if (v.getBehaviourTags() != null && !v.getBehaviourTags().isEmpty()) {
                behaviourTagName = v.getBehaviourTags().get(0);
              }
              if (snapshot.getStateMachines() != null
                  && snapshot.getStateMachines().containsKey(v.getId())) {
                userCTAAndStateMachineList.add(
                    new UserCTAAndStateMachineResponse(
                        v.getId().toString(),
                        ruleMapper.apply(v.getRule()),
                        snapshot.getStateMachines().get(v.getId()).getActiveStateMachines(),
                        snapshot.getStateMachines().get(v.getId()).getResetAt(),
                        snapshot.getStateMachines().get(v.getId()).getActionDoneAt(),
                        behaviourTagName));
              } else {
                userCTAAndStateMachineList.add(
                    new UserCTAAndStateMachineResponse(
                        v.getId().toString(),
                        ruleMapper.apply(v.getRule()),
                        Collections.emptyMap(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        behaviourTagName));
              }
              if (!behaviourTagName.isEmpty()) {
                behaviourTagNames.add(behaviourTagName);
              }
            });

    behaviourTagNames.forEach(
        name -> {
          if (snapshot.getBehaviourTags() != null
              && snapshot.getBehaviourTags().containsKey(name)) {
            behaviourTagSnapshots.add(
                new BehaviourTagSnapshot(
                    name,
                    new BehaviourExposureRule(
                        behaviourTagMap.get(name).getExposureRule() != null
                            ? behaviourTagMap.get(name).getExposureRule().getSession()
                            : new SessionFrequency(),
                        behaviourTagMap.get(name).getExposureRule() != null
                            ? behaviourTagMap.get(name).getExposureRule().getLifespan()
                            : new LifespanFrequency(),
                        behaviourTagMap.get(name).getExposureRule() != null
                            ? behaviourTagMap.get(name).getExposureRule().getWindow()
                            : new WindowFrequency(),
                        snapshot.getBehaviourTags().get(name).getExposureRule() != null
                            ? snapshot
                                .getBehaviourTags()
                                .get(name)
                                .getExposureRule()
                                .getCtasResetAt()
                            : Collections.emptyList()),
                    new CTARelationSnapshot(
                        behaviourTagMap.get(name).getCtaRelation() != null
                            ? behaviourTagMap.get(name).getCtaRelation().getShownCta()
                            : new CtaRelationRule(),
                        behaviourTagMap.get(name).getCtaRelation() != null
                            ? behaviourTagMap.get(name).getCtaRelation().getHideCta()
                            : new CtaRelationRule(),
                        snapshot.getBehaviourTags().get(name).getCtaRelation().getActiveCtas())));
          } else {
            try {
              behaviourTagSnapshots.add(
                  new BehaviourTagSnapshot(
                      name,
                      behaviourExposureRuleMapper.apply(
                          behaviourTagMap.get(name).getExposureRule()),
                      ctaRelationMapper.apply(behaviourTagMap.get(name).getCtaRelation())));
            } catch (Exception e) {
              behaviourTagSnapshots.add(
                  new BehaviourTagSnapshot(
                      name,
                      new BehaviourExposureRule(
                          new SessionFrequency(),
                          new LifespanFrequency(),
                          new WindowFrequency(),
                          Collections.emptyList()),
                      new CTARelationSnapshot(
                          new CtaRelationRule(), new CtaRelationRule(), Collections.emptyList())));
            }
          }
        });
    return new CTAResponse(userCTAAndStateMachineList, behaviourTagSnapshots);
  }
}
