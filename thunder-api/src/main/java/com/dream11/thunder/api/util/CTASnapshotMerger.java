package com.dream11.thunder.api.util;

import com.dream11.thunder.api.io.response.CTAResponse;
import com.dream11.thunder.api.io.response.UserCTAAndStateMachineResponse;
import com.dream11.thunder.api.model.BehaviourTagSnapshot;
import com.dream11.thunder.api.model.CTARelationSnapshot;
import com.dream11.thunder.api.model.BehaviourExposureRule;
import com.dream11.thunder.api.service.sdk.BehaviourExposureRuleMapper;
import com.dream11.thunder.api.service.sdk.CTARelationMapper;
import com.dream11.thunder.api.service.sdk.RuleMapper;
import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CtaRelationRule;
import com.dream11.thunder.core.model.rule.LifespanFrequency;
import com.dream11.thunder.core.model.rule.SessionFrequency;
import com.dream11.thunder.core.model.rule.WindowFrequency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for merging CTAs with user snapshots to create responses.
 */
public final class CTASnapshotMerger {

  private final RuleMapper ruleMapper;
  private final BehaviourExposureRuleMapper behaviourExposureRuleMapper;
  private final CTARelationMapper ctaRelationMapper;

  public CTASnapshotMerger(
      RuleMapper ruleMapper,
      BehaviourExposureRuleMapper behaviourExposureRuleMapper,
      CTARelationMapper ctaRelationMapper) {
    this.ruleMapper = ruleMapper;
    this.behaviourExposureRuleMapper = behaviourExposureRuleMapper;
    this.ctaRelationMapper = ctaRelationMapper;
  }

  /**
   * Merges active CTAs with user snapshot to create a CTAResponse.
   *
   * @param behaviourTagMap map of behaviour tags
   * @param activeCTAs map of active CTAs
   * @param snapshot user data snapshot
   * @return CTAResponse containing user CTAs and behaviour tag snapshots
   */
  public CTAResponse mergeCTAWithSnapshot(
      Map<String, BehaviourTag> behaviourTagMap,
      Map<Long, CTA> activeCTAs,
      com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    List<UserCTAAndStateMachineResponse> userCTAList = buildUserCTAList(activeCTAs, snapshot);
    List<BehaviourTagSnapshot> behaviourTagSnapshots =
        buildBehaviourTagSnapshots(behaviourTagMap, snapshot, extractBehaviourTagNames(activeCTAs));

    return new CTAResponse(userCTAList, behaviourTagSnapshots);
  }

  /**
   * Builds the list of user CTA responses from active CTAs and snapshot.
   */
  private List<UserCTAAndStateMachineResponse> buildUserCTAList(
      Map<Long, CTA> activeCTAs, com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    return activeCTAs.values().stream()
        .map(cta -> createUserCTAResponse(cta, snapshot))
        .collect(Collectors.toList());
  }

  /**
   * Creates a user CTA response for a single CTA.
   */
  private UserCTAAndStateMachineResponse createUserCTAResponse(
      CTA cta, com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    String behaviourTagName = extractFirstBehaviourTag(cta);
    com.dream11.thunder.api.model.StateMachineSnapshot stateMachineSnapshot =
        snapshot.getStateMachines() != null ? snapshot.getStateMachines().get(cta.getId()) : null;

    if (stateMachineSnapshot != null) {
      return new UserCTAAndStateMachineResponse(
          cta.getId().toString(),
          ruleMapper.apply(cta.getRule()),
          stateMachineSnapshot.getActiveStateMachines(),
          stateMachineSnapshot.getResetAt(),
          stateMachineSnapshot.getActionDoneAt(),
          behaviourTagName);
    } else {
      return new UserCTAAndStateMachineResponse(
          cta.getId().toString(),
          ruleMapper.apply(cta.getRule()),
          Collections.emptyMap(),
          Collections.emptyList(),
          Collections.emptyList(),
          behaviourTagName);
    }
  }

  /**
   * Extracts the first behaviour tag from a CTA, or empty string if none.
   */
  private String extractFirstBehaviourTag(CTA cta) {
    if (cta.getBehaviourTags() != null && !cta.getBehaviourTags().isEmpty()) {
      return cta.getBehaviourTags().get(0);
    }
    return "";
  }

  /**
   * Extracts all unique behaviour tag names from active CTAs.
   */
  private Set<String> extractBehaviourTagNames(Map<Long, CTA> activeCTAs) {
    return activeCTAs.values().stream()
        .filter(cta -> cta.getBehaviourTags() != null && !cta.getBehaviourTags().isEmpty())
        .flatMap(cta -> cta.getBehaviourTags().stream())
        .collect(Collectors.toSet());
  }

  /**
   * Builds behaviour tag snapshots from behaviour tag map and user snapshot.
   */
  private List<BehaviourTagSnapshot> buildBehaviourTagSnapshots(
      Map<String, BehaviourTag> behaviourTagMap,
      com.dream11.thunder.api.model.UserDataSnapshot snapshot,
      Set<String> behaviourTagNames) {
    List<BehaviourTagSnapshot> snapshots = new ArrayList<>();

    for (String tagName : behaviourTagNames) {
      BehaviourTagSnapshot tagSnapshot =
          createBehaviourTagSnapshot(tagName, behaviourTagMap, snapshot);
      if (tagSnapshot != null) {
        snapshots.add(tagSnapshot);
      }
    }

    return snapshots;
  }

  /**
   * Creates a behaviour tag snapshot, merging server data with user snapshot if available.
   */
  private BehaviourTagSnapshot createBehaviourTagSnapshot(
      String tagName,
      Map<String, BehaviourTag> behaviourTagMap,
      com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    BehaviourTag behaviourTag = behaviourTagMap.get(tagName);
    if (behaviourTag == null) {
      return null;
    }

    // Check if user has existing snapshot for this tag
    if (snapshot.getBehaviourTags() != null
        && snapshot.getBehaviourTags().containsKey(tagName)) {
      return createMergedBehaviourTagSnapshot(tagName, behaviourTag, snapshot);
    } else {
      return createNewBehaviourTagSnapshot(tagName, behaviourTag);
    }
  }

  /**
   * Creates a merged behaviour tag snapshot combining server data with user snapshot.
   */
  private BehaviourTagSnapshot createMergedBehaviourTagSnapshot(
      String tagName,
      BehaviourTag behaviourTag,
      com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    com.dream11.thunder.api.model.BehaviourTagSnapshot userSnapshot =
        snapshot.getBehaviourTags().get(tagName);

    BehaviourExposureRule exposureRule =
        new BehaviourExposureRule(
            behaviourTag.getExposureRule() != null
                ? behaviourTag.getExposureRule().getSession()
                : new SessionFrequency(),
            behaviourTag.getExposureRule() != null
                ? behaviourTag.getExposureRule().getLifespan()
                : new LifespanFrequency(),
            behaviourTag.getExposureRule() != null
                ? behaviourTag.getExposureRule().getWindow()
                : new WindowFrequency(),
            userSnapshot.getExposureRule() != null
                ? userSnapshot.getExposureRule().getCtasResetAt()
                : Collections.emptyList());

    CTARelationSnapshot ctaRelation =
        new CTARelationSnapshot(
            behaviourTag.getCtaRelation() != null
                ? behaviourTag.getCtaRelation().getShownCta()
                : new CtaRelationRule(),
            behaviourTag.getCtaRelation() != null
                ? behaviourTag.getCtaRelation().getHideCta()
                : new CtaRelationRule(),
            userSnapshot.getCtaRelation().getActiveCtas());

    return new BehaviourTagSnapshot(tagName, exposureRule, ctaRelation);
  }

  /**
   * Creates a new behaviour tag snapshot from server data only.
   */
  private BehaviourTagSnapshot createNewBehaviourTagSnapshot(
      String tagName, BehaviourTag behaviourTag) {
    try {
      return new BehaviourTagSnapshot(
          tagName,
          behaviourExposureRuleMapper.apply(behaviourTag.getExposureRule()),
          ctaRelationMapper.apply(behaviourTag.getCtaRelation()));
    } catch (Exception e) {
      // Return default snapshot on error
      return new BehaviourTagSnapshot(
          tagName,
          new BehaviourExposureRule(
              new SessionFrequency(),
              new LifespanFrequency(),
              new WindowFrequency(),
              Collections.emptyList()),
          new CTARelationSnapshot(
              new CtaRelationRule(), new CtaRelationRule(), Collections.emptyList()));
    }
  }
}

