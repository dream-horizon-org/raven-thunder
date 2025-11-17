package com.dream11.thunder.api.util;

import com.dream11.thunder.api.model.StateMachine;
import com.dream11.thunder.api.model.StateMachineSnapshot;
import com.dream11.thunder.core.model.CTA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for state machine operations and data archiving.
 */
public final class StateMachineUtil {

  private StateMachineUtil() {
    // Utility class - prevent instantiation
  }

  /**
   * Archives stale state machine data by removing CTAs that are no longer active or paused,
   * and removing expired state machines based on TTL.
   *
   * @param activeCTAs map of active CTAs
   * @param pausedCTAs map of paused CTAs
   * @param snapshot the user data snapshot to clean
   * @return true if any updates were made, false otherwise
   */
  public static boolean archiveStaleData(
      Map<Long, CTA> activeCTAs,
      Map<Long, CTA> pausedCTAs,
      com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    if (snapshot.getStateMachines() == null) {
      return false;
    }

    boolean updated = false;
    Map<Long, StateMachineSnapshot> stateMachineSnapshot = snapshot.getStateMachines();

    // Collect active behaviour tags from active/paused CTAs
    Set<String> activeBehaviourTags = collectActiveBehaviourTags(activeCTAs, pausedCTAs);

    // Remove CTAs that are no longer active or paused
    updated |= removeInactiveCTAs(stateMachineSnapshot, activeCTAs, pausedCTAs);

    // Remove expired state machines based on TTL
    updated |= removeExpiredStateMachines(stateMachineSnapshot, activeCTAs);

    // Remove stale behaviour tags
    updated |= removeStaleBehaviourTags(snapshot, activeBehaviourTags);

    return updated;
  }

  /**
   * Collects all active behaviour tags from active and paused CTAs.
   */
  private static Set<String> collectActiveBehaviourTags(
      Map<Long, CTA> activeCTAs, Map<Long, CTA> pausedCTAs) {
    Set<String> activeBehaviourTags = new HashSet<>();

    activeCTAs.values().forEach(cta -> activeBehaviourTags.addAll(cta.getBehaviourTags()));
    pausedCTAs.values().forEach(cta -> activeBehaviourTags.addAll(cta.getBehaviourTags()));

    return activeBehaviourTags;
  }

  /**
   * Removes CTAs from snapshot that are no longer active or paused.
   */
  private static boolean removeInactiveCTAs(
      Map<Long, StateMachineSnapshot> stateMachineSnapshot,
      Map<Long, CTA> activeCTAs,
      Map<Long, CTA> pausedCTAs) {
    List<Long> ctaIds = new ArrayList<>(stateMachineSnapshot.keySet());
    boolean updated = false;

    for (Long ctaId : ctaIds) {
      if (!activeCTAs.containsKey(ctaId) && !pausedCTAs.containsKey(ctaId)) {
        stateMachineSnapshot.remove(ctaId);
        updated = true;
      }
    }

    return updated;
  }

  /**
   * Removes expired state machines based on TTL rules.
   */
  private static boolean removeExpiredStateMachines(
      Map<Long, StateMachineSnapshot> stateMachineSnapshot, Map<Long, CTA> activeCTAs) {
    boolean updated = false;
    long currentTime = System.currentTimeMillis();

    for (Map.Entry<Long, StateMachineSnapshot> entry : stateMachineSnapshot.entrySet()) {
      Long ctaId = entry.getKey();
      StateMachineSnapshot snapshot = entry.getValue();
      CTA cta = activeCTAs.get(ctaId);

      if (cta != null && cta.getRule() != null && cta.getRule().getStateMachineTTL() != null) {
        Long ttl = cta.getRule().getStateMachineTTL();
        List<String> groupIds = new ArrayList<>(snapshot.getActiveStateMachines().keySet());

        for (String groupId : groupIds) {
          StateMachine stateMachine = snapshot.getActiveStateMachines().get(groupId);
          if (stateMachine != null && (currentTime - stateMachine.getCreatedAt()) > ttl) {
            snapshot.getActiveStateMachines().remove(groupId);
            updated = true;
          }
        }
      }
    }

    return updated;
  }

  /**
   * Removes behaviour tags that are no longer active.
   */
  private static boolean removeStaleBehaviourTags(
      com.dream11.thunder.api.model.UserDataSnapshot snapshot, Set<String> activeBehaviourTags) {
    if (snapshot.getBehaviourTags() == null) {
      return false;
    }

    Set<String> existingTags = new HashSet<>(snapshot.getBehaviourTags().keySet());
    List<String> tagsToRemove = new ArrayList<>();

    for (String tag : existingTags) {
      if (!activeBehaviourTags.contains(tag)) {
        tagsToRemove.add(tag);
      }
    }

    tagsToRemove.forEach(snapshot.getBehaviourTags()::remove);
    return !tagsToRemove.isEmpty();
  }

  /**
   * Merges a delta snapshot into the existing snapshot, updating state machines and behaviour tags.
   *
   * @param snapshot the existing snapshot to merge into
   * @param deltaSnapshot the delta snapshot to merge
   */
  public static void mergeDeltaSnapshot(
      com.dream11.thunder.api.model.UserDataSnapshot snapshot,
      com.dream11.thunder.api.io.request.CTASnapshotRequest deltaSnapshot) {
    initializeSnapshotMaps(snapshot);

    if (deltaSnapshot.getCtas() != null) {
      mergeStateMachineSnapshots(snapshot, deltaSnapshot.getCtas());
    }

    if (deltaSnapshot.getBehaviourTags() != null) {
      mergeBehaviourTagSnapshots(snapshot, deltaSnapshot.getBehaviourTags());
    }
  }

  /**
   * Initializes snapshot maps if they are null.
   */
  private static void initializeSnapshotMaps(
      com.dream11.thunder.api.model.UserDataSnapshot snapshot) {
    if (snapshot.getStateMachines() == null) {
      snapshot.setStateMachines(new HashMap<>());
    }
    if (snapshot.getBehaviourTags() == null) {
      snapshot.setBehaviourTags(new HashMap<>());
    }
  }

  /**
   * Merges state machine snapshots from delta into existing snapshot.
   */
  private static void mergeStateMachineSnapshots(
      com.dream11.thunder.api.model.UserDataSnapshot snapshot,
      List<StateMachineSnapshot> deltaCTAs) {
    for (StateMachineSnapshot ctaDelta : deltaCTAs) {
      Long ctaId = Long.parseLong(ctaDelta.getCtaId());

      if (!snapshot.getStateMachines().containsKey(ctaId)) {
        snapshot.getStateMachines().put(ctaId, ctaDelta);
        continue;
      }

      mergeStateMachinesForCTA(snapshot.getStateMachines().get(ctaId), ctaDelta);
      updateCTAMetadata(snapshot.getStateMachines().get(ctaId), ctaDelta);
    }

    resetStateMachines(snapshot.getStateMachines(), deltaCTAs);
  }

  /**
   * Merges individual state machines for a CTA.
   */
  private static void mergeStateMachinesForCTA(
      StateMachineSnapshot existing, StateMachineSnapshot delta) {
    Map<String, StateMachine> existingSMs = existing.getActiveStateMachines();

    for (Map.Entry<String, StateMachine> smDelta : delta.getActiveStateMachines().entrySet()) {
      String groupId = smDelta.getKey();
      StateMachine deltaSM = smDelta.getValue();

      if (!existingSMs.containsKey(groupId)) {
        existingSMs.put(groupId, deltaSM);
        continue;
      }

      // Update if delta has newer transition time
      StateMachine existingSM = existingSMs.get(groupId);
      if (existingSM.getLastTransitionAt() <= deltaSM.getLastTransitionAt()) {
        existingSMs.put(groupId, deltaSM);
      }
    }
  }

  /**
   * Updates CTA metadata (resetAt, actionDoneAt) from delta.
   */
  private static void updateCTAMetadata(
      StateMachineSnapshot existing, StateMachineSnapshot delta) {
    existing.setResetAt(delta.getResetAt());
    existing.setActionDoneAt(delta.getActionDoneAt());
  }

  /**
   * Resets state machines based on reset flags in delta snapshot.
   */
  private static void resetStateMachines(
      Map<Long, StateMachineSnapshot> snapshot, List<StateMachineSnapshot> deltaSnapshot) {
    for (StateMachineSnapshot ctaDelta : deltaSnapshot) {
      Long ctaId = Long.parseLong(ctaDelta.getCtaId());
      StateMachineSnapshot existing = snapshot.get(ctaId);

      if (existing == null) {
        continue;
      }

      Map<String, StateMachine> existingSMs = existing.getActiveStateMachines();

      for (Map.Entry<String, StateMachine> smDelta : ctaDelta.getActiveStateMachines().entrySet()) {
        String groupId = smDelta.getKey();
        StateMachine deltaSM = smDelta.getValue();

        if (existingSMs.containsKey(groupId)
            && existingSMs.get(groupId).getLastTransitionAt() <= deltaSM.getLastTransitionAt()
            && deltaSM.getReset() != null
            && deltaSM.getReset()) {
          existingSMs.remove(groupId);
        }
      }
    }
  }

  /**
   * Merges behaviour tag snapshots from delta into existing snapshot.
   */
  private static void mergeBehaviourTagSnapshots(
      com.dream11.thunder.api.model.UserDataSnapshot snapshot,
      List<com.dream11.thunder.api.model.BehaviourTagSnapshot> deltaBehaviourTags) {
    if (snapshot.getBehaviourTags() == null) {
      snapshot.setBehaviourTags(new HashMap<>());
    }

    for (com.dream11.thunder.api.model.BehaviourTagSnapshot behaviourTagSnapshot :
        deltaBehaviourTags) {
      snapshot
          .getBehaviourTags()
          .put(behaviourTagSnapshot.getBehaviourTagName(), behaviourTagSnapshot);
    }
  }
}

