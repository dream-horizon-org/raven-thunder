package com.dream11.thunder.admin.service.behaviourTag;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.exception.ErrorEntity;
import com.dream11.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.admin.io.response.BehaviourTagsResponse;
import com.dream11.thunder.admin.service.BehaviourTagService;
import com.dream11.thunder.core.dao.BehaviourTagsRepository;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
            it -> {
              List<String> invalidCTAList = new ArrayList<>();
              AtomicBoolean invalid = new AtomicBoolean(false);
              it.values()
                  .forEach(
                      item -> {
                        if (behaviourTag.getLinkedCtas().contains(item.getId().toString())) {
                          if (item.getCtaStatus().equals(CTAStatus.LIVE)
                              || item.getCtaStatus().equals(CTAStatus.SCHEDULED)
                              || item.getCtaStatus().equals(CTAStatus.CONCLUDED)
                              || item.getCtaStatus().equals(CTAStatus.TERMINATED)) {
                            invalid.set(true);
                            invalidCTAList.add(item.getName());
                          }
                        }
                      });
              if (invalid.get()) {
                log.info(
                    "Error in Behaviour tags linking for BT : {} , ctas {}",
                    behaviourTag.getBehaviourTagName(),
                    invalidCTAList);
                throw new DefinedException(
                    ErrorEntity.INVALID_BEHAVIOUR_TAG_CREATION,
                    "Only Draft/Paused CTAs can be attached to new Behaviour tags. CTAs with invalid statuses: "
                        + invalidCTAList);
              }
            })
        .flatMapCompletable(
            result ->
                behaviourTagRepository
                    .create(tenantId, behaviourTagMapper.apply(tenantId, behaviourTag, userId))
                    .doOnComplete(
                        () -> {
                          log.info(
                              "Behaviour tag BT : {} created with ctas {}",
                              behaviourTag.getBehaviourTagName(),
                              behaviourTag.getLinkedCtas());
                          List<Long> ctaIdList = new ArrayList<>();
                          behaviourTag.getLinkedCtas().stream()
                              .map(
                                  cta -> {
                                    ctaIdList.add(Long.parseLong(cta));
                                    return ctaIdList;
                                  })
                              .forEach(
                                  ctaIds ->
                                      ctaRepository
                                          .findAll(tenantId)
                                          .map(
                                              ctaMap ->
                                                  ctaMap.entrySet().stream()
                                                      .filter(
                                                          entry ->
                                                              ctaIds.contains(
                                                                  entry.getValue().getId()))
                                                      .filter(
                                                          entry ->
                                                              entry
                                                                      .getValue()
                                                                      .getCtaStatus()
                                                                      .equals(CTAStatus.DRAFT)
                                                                  || entry
                                                                      .getValue()
                                                                      .getCtaStatus()
                                                                      .equals(CTAStatus.PAUSED))
                                                      .collect(
                                                          Collectors.toMap(
                                                              Map.Entry::getKey,
                                                              Map.Entry::getValue)))
                                          .map(
                                              ctaMap -> {
                                                for (CTA cta : ctaMap.values()) {
                                                  ctaRepository
                                                      .update(
                                                          cta.getId(),
                                                          List.of(
                                                              behaviourTag.getBehaviourTagName()))
                                                      .doOnComplete(
                                                          () ->
                                                              log.info(
                                                                  "linked BT info updated for ctaId :"
                                                                      + cta.getId()
                                                                      + " ..."))
                                                      .doOnError(
                                                          error -> {
                                                            log.error(
                                                                "error while updating linked BT info for CTA id {}",
                                                                cta.getId(),
                                                                error);
                                                          })
                                                      .doOnSubscribe(
                                                          disposable ->
                                                              log.info(
                                                                  "updating linked cta info..."))
                                                      .subscribe();
                                                }

                                                return Completable.complete();
                                              })
                                          .subscribe());
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
            it -> {
              List<String> invalidCTAList = new ArrayList<>();
              AtomicBoolean invalid = new AtomicBoolean(false);
              it.values()
                  .forEach(
                      item -> {
                        if (behaviourTag.getLinkedCtas().contains(item.getId().toString())) {
                          if (item.getCtaStatus().equals(CTAStatus.LIVE)
                              || item.getCtaStatus().equals(CTAStatus.SCHEDULED)) {
                            invalid.set(true);
                            invalidCTAList.add(item.getName());
                          }
                        }
                      });
              if (invalid.get()) {
                log.info(
                    "Error in Behaviour tags linking for BT : {} , ctas {}",
                    behaviourTagName,
                    invalidCTAList);
                throw new DefinedException(
                    ErrorEntity.INVALID_BEHAVIOUR_TAG_UPDATION,
                    "Only Draft/Paused CTAs can be attached to new Behaviour tags. CTAs with invalid statuses: "
                        + invalidCTAList);
              }
            })
        .flatMapCompletable(
            result ->
                behaviourTagRepository
                    .find(tenantId, behaviourTagName)
                    .switchIfEmpty(
                        Single.error(new DefinedException(ErrorEntity.NO_SUCH_BEHAVIOUR_TAG)))
                    .flatMapCompletable(
                        it -> {
                          Set<String> linkedCtas = it.getLinkedCtas();
                          List<String> unlinkedCtas = new ArrayList<>(linkedCtas);
                          List<String> newLinkedCtas = new ArrayList<>();
                          Set<String> incomingCtas = behaviourTag.getLinkedCtas();

                          linkedCtas.forEach(
                              cta -> {
                                if (incomingCtas.contains(cta)) {
                                  unlinkedCtas.remove(cta);
                                }
                              });
                          incomingCtas.forEach(
                              cta -> {
                                if (!linkedCtas.contains(cta)) {
                                  newLinkedCtas.add(cta);
                                }
                              });
                          for (String ctaId : unlinkedCtas) {
                            ctaRepository
                                .update(Long.parseLong(ctaId), Collections.emptyList())
                                .doOnComplete(
                                    () -> log.info("Behaviour tags unlinked for cta id {}", ctaId))
                                .doOnError(
                                    error ->
                                        log.info(
                                            "Error unlinking behaviour tags for cta id {}",
                                            ctaId,
                                            error))
                                .subscribe();
                          }

                          for (String ctaId : incomingCtas) {
                            ctaRepository
                                .update(Long.parseLong(ctaId), List.of(behaviourTagName))
                                .doOnComplete(
                                    () -> log.info("Behaviour tags unlinked for cta id {}", ctaId))
                                .doOnError(
                                    error ->
                                        log.info(
                                            "Error unlinking behaviour tags for cta id {}",
                                            ctaId,
                                            error))
                                .subscribe();
                          }
                          behaviourTagRepository
                              .update(
                                  tenantId,
                                  behaviourTagName,
                                  behaviourTagUpdateMapper.apply(
                                      tenantId, behaviourTag, behaviourTagName, userId))
                              .doOnComplete(
                                  () -> {
                                    log.info(
                                        "Behaviour tag BT : {} updated with ctas {}",
                                        behaviourTagName,
                                        behaviourTag.getLinkedCtas());
                                    for (String ctaId : newLinkedCtas) {
                                      ctaRepository
                                          .update(Long.parseLong(ctaId), List.of(behaviourTagName))
                                          .subscribe();
                                    }
                                  })
                              .subscribe();
                          return Completable.complete();
                        }));
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
}

