package com.dream11.thunder.admin.service.behaviourTag;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.exception.ErrorEntity;
import com.dream11.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.admin.io.response.BehaviourTagsResponse;
import com.dream11.thunder.admin.service.BehaviourTagService;
import com.dream11.thunder.admin.util.CTALinkingHelper;
import com.dream11.thunder.admin.util.CTAValidationHelper;
import com.dream11.thunder.core.dao.BehaviourTagsRepository;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.model.BehaviourTag;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BehaviourTagServiceImpl implements BehaviourTagService {
  private final BehaviourTagsRepository behaviourTagRepository;
  private final CTARepository ctaRepository;

  private final BehaviourTagMapper behaviourTagMapper = new BehaviourTagMapper();
  private final BehaviourTagUpdateMapper behaviourTagUpdateMapper = new BehaviourTagUpdateMapper();

  @Inject
  public BehaviourTagServiceImpl(
      BehaviourTagsRepository behaviourTagRepository, CTARepository ctaRepository) {
    this.behaviourTagRepository = behaviourTagRepository;
    this.ctaRepository = ctaRepository;
  }

  @SneakyThrows
  @Override
  public Completable createBehaviourTag(
      String tenantId, String userId, BehaviourTagCreateRequest behaviourTag) {
    return ctaRepository
        .findAll(tenantId)
        .doOnSuccess(
            allCTAs ->
                CTAValidationHelper.validateCTAsForNewBehaviourTag(
                    allCTAs, behaviourTag.getLinkedCtas(), behaviourTag.getBehaviourTagName()))
        .flatMapCompletable(
            allCTAs ->
                behaviourTagRepository
                    .create(tenantId, behaviourTagMapper.apply(tenantId, behaviourTag, userId))
                    .doOnComplete(
                        () -> {
                          log.info(
                              "Behaviour tag BT : {} created with ctas {}",
                              behaviourTag.getBehaviourTagName(),
                              behaviourTag.getLinkedCtas());
                          linkCTAsToNewBehaviourTag(tenantId, behaviourTag);
                        }));
  }

  @SneakyThrows
  @Override
  public Completable updateBehaviourTag(
      String tenantId,
      String behaviourTagName,
      BehaviourTagPutRequest behaviourTag,
      String userId) {
    return ctaRepository
        .findAll(tenantId)
        .doOnSuccess(
            allCTAs ->
                CTAValidationHelper.validateCTAsForBehaviourTagUpdate(
                    allCTAs, behaviourTag.getLinkedCtas(), behaviourTagName))
        .flatMapCompletable(
            allCTAs ->
                behaviourTagRepository
                    .find(tenantId, behaviourTagName)
                    .switchIfEmpty(
                        Single.error(new DefinedException(ErrorEntity.NO_SUCH_BEHAVIOUR_TAG)))
                    .flatMapCompletable(
                        existingTag ->
                            updateBehaviourTagWithCTALinks(
                                tenantId,
                                behaviourTagName,
                                behaviourTag,
                                existingTag.getLinkedCtas(),
                                userId)));
  }

  @Override
  public Single<BehaviourTagsResponse> fetchAllBehaviourTags(String tenantId) {
    return behaviourTagRepository
        .findAll(tenantId)
        .map(
            it -> {
              BehaviourTagsResponse ctaListResponse = new BehaviourTagsResponse();
              ctaListResponse.setBehaviourTags(new ArrayList<>(it.values()));
              return ctaListResponse;
            });
  }

  @Override
  public Single<BehaviourTag> fetchBehaviourTagDetail(String tenantId, String behaviourTagName) {
    return behaviourTagRepository
        .find(tenantId, behaviourTagName)
        .switchIfEmpty(
            Single.defer(
                () -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_BEHAVIOUR_TAG))));
  }

  private void linkCTAsToNewBehaviourTag(
      String tenantId, BehaviourTagCreateRequest behaviourTag) {
    List<Long> ctaIds =
        behaviourTag.getLinkedCtas().stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());

    CTALinkingHelper.linkCTAsToBehaviourTag(
            ctaRepository, tenantId, ctaIds, behaviourTag.getBehaviourTagName())
        .subscribe(
            () -> log.debug("CTAs linked to behaviour tag: {}", behaviourTag.getBehaviourTagName()),
            error ->
                log.error(
                    "Error linking CTAs to behaviour tag: {}",
                    behaviourTag.getBehaviourTagName(),
                    error));
  }

  private Completable updateBehaviourTagWithCTALinks(
      String tenantId,
      String behaviourTagName,
      BehaviourTagPutRequest behaviourTag,
      Set<String> existingLinkedCtas,
      String userId) {
    CTALinkingHelper.CTALinkDiff linkDiff =
        CTALinkingHelper.calculateLinkDiff(existingLinkedCtas, behaviourTag.getLinkedCtas());

    // Unlink removed CTAs
    CTALinkingHelper.unlinkCTAsFromBehaviourTag(ctaRepository, linkDiff.getUnlinkedCtas())
        .subscribe(
            () -> log.debug("Unlinked CTAs from behaviour tag: {}", behaviourTagName),
            error -> log.error("Error unlinking CTAs from behaviour tag: {}", behaviourTagName, error));

    // Update the behaviour tag
    return behaviourTagRepository
        .update(
            tenantId,
            behaviourTagName,
            behaviourTagUpdateMapper.apply(tenantId, behaviourTag, behaviourTagName, userId))
        .doOnComplete(
            () -> {
              log.info(
                  "Behaviour tag BT : {} updated with ctas {}",
                  behaviourTagName,
                  behaviourTag.getLinkedCtas());
              // Link new CTAs
              linkNewCTAsToBehaviourTag(tenantId, behaviourTagName, linkDiff.getNewlyLinkedCtas());
            });
  }

  private void linkNewCTAsToBehaviourTag(
      String tenantId, String behaviourTagName, List<String> newCtaIds) {
    if (newCtaIds.isEmpty()) {
      return;
    }

    List<Long> ctaIds = newCtaIds.stream().map(Long::parseLong).collect(Collectors.toList());
    CTALinkingHelper.linkCTAsToBehaviourTag(ctaRepository, tenantId, ctaIds, behaviourTagName)
        .subscribe(
            () -> log.debug("New CTAs linked to behaviour tag: {}", behaviourTagName),
            error ->
                log.error(
                    "Error linking new CTAs to behaviour tag: {}", behaviourTagName, error));
  }
}

