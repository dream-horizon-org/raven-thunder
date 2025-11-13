package com.dream11.thunder.admin.util;

import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.model.CTA;
import io.reactivex.rxjava3.core.Completable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling CTA linking and unlinking operations with behaviour tags.
 */
@Slf4j
public final class CTALinkingHelper {

  private CTALinkingHelper() {
    // Utility class - prevent instantiation
  }

  /**
   * Links CTAs to a behaviour tag by updating their behaviour tag list.
   * Only processes CTAs with DRAFT or PAUSED status.
   *
   * @param ctaRepository the CTA repository
   * @param tenantId the tenant ID
   * @param ctaIds the list of CTA IDs to link
   * @param behaviourTagName the behaviour tag name to link
   * @return Completable that completes when all CTAs are linked
   */
  public static Completable linkCTAsToBehaviourTag(
      CTARepository ctaRepository,
      String tenantId,
      List<Long> ctaIds,
      String behaviourTagName) {
    if (ctaIds.isEmpty()) {
      return Completable.complete();
    }

    return ctaRepository
        .findAll(tenantId)
        .map(
            ctaMap ->
                ctaMap.entrySet().stream()
                    .filter(entry -> ctaIds.contains(entry.getValue().getId()))
                    .filter(
                        entry ->
                            CTAStatusValidator.isValidForBehaviourTagLink(
                                entry.getValue().getCtaStatus()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
        .flatMapCompletable(
            filteredCtaMap -> {
              for (CTA cta : filteredCtaMap.values()) {
                updateCTABehaviourTag(
                    ctaRepository, cta.getId(), List.of(behaviourTagName), cta.getId());
              }
              return Completable.complete();
            });
  }

  /**
   * Unlinks CTAs from a behaviour tag by clearing their behaviour tag list.
   *
   * @param ctaRepository the CTA repository
   * @param ctaIds the list of CTA IDs to unlink
   * @return Completable that completes when all CTAs are unlinked
   */
  public static Completable unlinkCTAsFromBehaviourTag(
      CTARepository ctaRepository, List<String> ctaIds) {
    if (ctaIds.isEmpty()) {
      return Completable.complete();
    }

    Completable[] unlinkOperations =
        ctaIds.stream()
            .map(
                ctaId ->
                    updateCTABehaviourTag(
                        ctaRepository,
                        Long.parseLong(ctaId),
                        Collections.emptyList(),
                        ctaId))
            .toArray(Completable[]::new);

    return Completable.mergeArray(unlinkOperations);
  }

  /**
   * Updates a single CTA's behaviour tag list.
   *
   * @param ctaRepository the CTA repository
   * @param ctaId the CTA ID
   * @param behaviourTags the list of behaviour tags to set
   * @param logIdentifier identifier for logging purposes
   * @return Completable that completes when the update is done
   */
  private static Completable updateCTABehaviourTag(
      CTARepository ctaRepository,
      Long ctaId,
      List<String> behaviourTags,
      Object logIdentifier) {
    return ctaRepository
        .update(ctaId, behaviourTags)
        .doOnComplete(
            () ->
                log.info(
                    "Behaviour tag {} updated for CTA id {}",
                    behaviourTags.isEmpty() ? "unlinked" : "linked",
                    logIdentifier))
        .doOnError(
            error ->
                log.error(
                    "Error updating behaviour tag for CTA id {}",
                    logIdentifier,
                    error));
  }

  /**
   * Calculates the difference between existing and incoming CTA links.
   *
   * @param existingCtas the set of existing linked CTA IDs
   * @param incomingCtas the set of incoming CTA IDs
   * @return a CTALinkDiff object containing unlinked and newly linked CTAs
   */
  public static CTALinkDiff calculateLinkDiff(Set<String> existingCtas, Set<String> incomingCtas) {
    List<String> unlinkedCtas =
        existingCtas.stream()
            .filter(cta -> !incomingCtas.contains(cta))
            .collect(Collectors.toList());

    List<String> newlyLinkedCtas =
        incomingCtas.stream()
            .filter(cta -> !existingCtas.contains(cta))
            .collect(Collectors.toList());

    return new CTALinkDiff(unlinkedCtas, newlyLinkedCtas);
  }

  /**
   * Data class to hold the difference between existing and incoming CTA links.
   */
  public static class CTALinkDiff {
    private final List<String> unlinkedCtas;
    private final List<String> newlyLinkedCtas;

    public CTALinkDiff(List<String> unlinkedCtas, List<String> newlyLinkedCtas) {
      this.unlinkedCtas = unlinkedCtas;
      this.newlyLinkedCtas = newlyLinkedCtas;
    }

    public List<String> getUnlinkedCtas() {
      return unlinkedCtas;
    }

    public List<String> getNewlyLinkedCtas() {
      return newlyLinkedCtas;
    }
  }
}

