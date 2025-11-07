package com.dream11.thunder.admin.service.admin;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.exception.ErrorEntity;
import com.dream11.thunder.admin.io.request.CTARequest;
import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.admin.io.response.CTAListResponse;
import com.dream11.thunder.admin.io.response.StatusWiseCount;
import com.dream11.thunder.admin.model.FilterProps;
import com.dream11.thunder.admin.service.AdminService;
import com.dream11.thunder.admin.service.filters.CTAFilters;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.dao.NudgePreviewRepository;
import com.dream11.thunder.core.dao.NudgeRepository;
import com.dream11.thunder.core.dao.cta.ActiveCTA;
import com.dream11.thunder.core.error.ServiceError;
import com.dream11.thunder.core.exception.ThunderException;
import com.dream11.thunder.core.io.response.FilterResponse;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import com.dream11.thunder.core.model.Nudge;
import com.dream11.thunder.core.model.NudgePreview;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminServiceImpl implements AdminService {

  private final CTARepository ctaRepository;
  private final NudgeRepository nudgeRepository;
  private final NudgePreviewRepository nudgePreviewRepository;
  private final CreateCTAMapper createCtaMapper = new CreateCTAMapper();
  private final CTAUpdateValidator ctaUpdateValidator = new CTAUpdateValidator();

  @Inject
  public AdminServiceImpl(
      NudgeRepository nudgeRepository,
      CTARepository ctaRepository,
      NudgePreviewRepository nudgePreviewRepository) {
    this.nudgeRepository = nudgeRepository;
    this.ctaRepository = ctaRepository;
    this.nudgePreviewRepository = nudgePreviewRepository;
  }

  @Override
  public Single<Long> createCTA(
      String tenantId, @NotNull @Valid CTARequest cta, @NotNull String user) {
    return fetchFilters(tenantId)
        .flatMap(
            filterResponse -> {
              if (filterResponse.getNames() != null
                  && filterResponse.getNames().contains(cta.getName())) {
                return Single.error(new DefinedException(ErrorEntity.DUPLICATE_NAME_NOT_PERMITTED));
              }
              return ctaRepository
                  .generatedIncrementId(tenantId)
                  .flatMap(
                      it ->
                          ctaRepository
                              .create(tenantId, createCtaMapper.apply(tenantId, cta, user, it))
                              .toSingleDefault(Long.parseLong(it.toString())))
                  .doOnSuccess(
                      ignore ->
                          ctaRepository
                              .updateFilters(
                                  tenantId, cta.getTags(), cta.getTeam(), cta.getName(), user)
                              .subscribe())
                  .onErrorResumeNext(
                      throwable ->
                          Single.error(new DefinedException(ErrorEntity.CTA_CREATION_ERROR)));
            });
  }

  @Override
  public Completable updateCTA(
      String tenantId, CTAUpdateRequest ctaRequest, Long ctaId, String user) {
    return ctaRepository
        .findWithGeneration(tenantId, ctaId)
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA))))
        .flatMapCompletable(
            ctaDetails -> {
              CTA cta = ctaUpdateValidator.apply(ctaRequest, ctaDetails, user, ctaId);
              return ctaRepository
                  .update(cta, ctaDetails.getGenerationId())
                  .onErrorResumeNext(
                      throwable ->
                          Completable.error(new DefinedException(ErrorEntity.CTA_UPDATE_ERROR)))
                  .doOnComplete(
                      () ->
                          ctaRepository
                              .updateFilters(
                                  tenantId,
                                  ctaRequest.getTags(),
                                  ctaRequest.getTeam(),
                                  cta.getName(),
                                  user)
                              .subscribe());
            });
  }

  @Override
  public Single<CTA> fetchCTA(String tenantId, Long ctaId) {
    return ctaRepository
        .find(tenantId, ctaId)
        .map(it -> it)
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA))));
  }

  @Override
  public Single<FilterResponse> fetchFilters(String tenantId) {
    return ctaRepository.findFilters(tenantId).switchIfEmpty(Single.just(new FilterResponse()));
  }

  @Override
  public Completable createOrUpdateNudgePreview(String tenantId, NudgePreview nudgePreview) {
    return nudgePreviewRepository
        .find(tenantId, nudgePreview.getId())
        .flatMapCompletable(r -> nudgePreviewRepository.createOrUpdate(tenantId, nudgePreview))
        .onErrorResumeNext(
            throwable -> {
              // If not found, createOrUpdate anyway
              if (throwable instanceof ThunderException
                  && ((ThunderException) throwable)
                      .getErrorCode()
                      .equals(ServiceError.NUDGE_PREVIEW_NOT_FOUND_EXCEPTION.getErrorCode())) {
                return nudgePreviewRepository.createOrUpdate(tenantId, nudgePreview);
              }
              return Completable.error(throwable);
            });
  }

  @Override
  public Single<CTAListResponse> fetchCTAs(
      String tenantId, FilterProps filterProps, int pageNumber, int pageSize) {
    return ctaRepository
        .findAll(tenantId)
        .map(
            it -> {
              List<CTA> ctas =
                  it.values().stream()
                      .filter(CTAFilters.filter(filterProps))
                      .collect(Collectors.toList());
              CTAListResponse ctaListResponse = new CTAListResponse();
              ctaListResponse.setStatusWiseCount(statusFinder(ctas));
              ctas = statusFilter(ctas, filterProps.getStatus());
              int totalEntries = ctas.size();
              ctas = paginate(ctas, pageNumber, pageSize);
              ctaListResponse.setCtas(ctas);
              ctaListResponse.setPageNumber(pageNumber);
              ctaListResponse.setPageSize(pageSize);
              ctaListResponse.setTotalPages(totalEntries / pageSize + 1);
              ctaListResponse.setTotalEntries(totalEntries);
              return ctaListResponse;
            });
  }

  private List<CTA> paginate(List<CTA> ctas, int pageNumber, int pageSize) {
    int ctaSize = ctas.size();
    int maxPages = ctaSize / pageSize;
    if (pageNumber > maxPages || pageNumber < 0) {
      ctas.clear();
    } else {
      int startIndex = Math.max(0, Math.min(pageNumber * pageSize, ctaSize - 1));
      int numberOfItems = Math.max(0, Math.min(pageSize, ctaSize - startIndex));
      ctas = ctas.subList(startIndex, startIndex + numberOfItems);
    }
    return ctas;
  }

  private StatusWiseCount statusFinder(List<CTA> ctas) {
    Map<CTAStatus, AtomicInteger> statuses = new HashMap<>();
    statuses.put(CTAStatus.DRAFT, new AtomicInteger(0));
    statuses.put(CTAStatus.PAUSED, new AtomicInteger(0));
    statuses.put(CTAStatus.LIVE, new AtomicInteger(0));
    statuses.put(CTAStatus.SCHEDULED, new AtomicInteger(0));
    statuses.put(CTAStatus.CONCLUDED, new AtomicInteger(0));
    statuses.put(CTAStatus.TERMINATED, new AtomicInteger(0));
    ctas.forEach(
        cta ->
            statuses
                .computeIfAbsent(cta.getCtaStatus(), k -> new AtomicInteger())
                .incrementAndGet());
    return new StatusWiseCount(
        statuses.get(CTAStatus.DRAFT),
        statuses.get(CTAStatus.PAUSED),
        statuses.get(CTAStatus.LIVE),
        statuses.get(CTAStatus.SCHEDULED),
        statuses.get(CTAStatus.CONCLUDED),
        statuses.get(CTAStatus.TERMINATED));
  }

  private List<CTA> statusFilter(List<CTA> ctas, String ctaStatus) {
    try {
      CTAStatus st = CTAStatus.valueOf(ctaStatus.toUpperCase());
      return ctas.stream()
          .filter(cta -> cta.getCtaStatus().equals(st))
          .collect(Collectors.toList());
    } catch (Exception e) {
      return ctas;
    }
  }

  @Override
  public Completable updateStatusToPaused(String tenantId, Long id) {
    return ctaRepository
        .find(tenantId, id)
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA))))
        .filter(
            cta ->
                (cta.getCtaStatus().equals(CTAStatus.LIVE)
                    || cta.getCtaStatus().equals(CTAStatus.SCHEDULED)))
        .switchIfEmpty(
            Single.defer(
                () -> Single.error(new DefinedException(ErrorEntity.INVALID_STATUS_UPDATE))))
        .flatMapCompletable(it -> ctaRepository.update(id, CTAStatus.PAUSED));
  }

  @Override
  public Completable updateStatusToLive(String tenantId, Long id) {
    return ctaRepository
        .find(tenantId, id)
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA))))
        .filter(
            cta ->
                cta.getCtaStatus().equals(CTAStatus.DRAFT)
                    || cta.getCtaStatus().equals(CTAStatus.PAUSED))
        .switchIfEmpty(
            Single.defer(
                () -> Single.error(new DefinedException(ErrorEntity.INVALID_STATUS_UPDATE))))
        .flatMapCompletable(
            cta ->
                ctaRepository
                    .update(
                        id,
                        CTAStatus.LIVE,
                        System.currentTimeMillis(),
                        cta.getEndTime() == null
                            ? System.currentTimeMillis() + com.dream11.thunder.admin.util.Constants.THREE_YEARS
                            : cta.getEndTime())
                    .doOnComplete(() -> log.info("CTA {} made live", id))
                    .doOnError(error -> log.error("Error making CTA {} live ", id, error)));
  }

  @Override
  public Completable updateStatusToScheduled(String tenantId, Long id) {
    return ctaRepository
        .findWithGeneration(tenantId, id)
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA))))
        .filter(
            cta ->
                (cta.getCtaStatus().equals(CTAStatus.DRAFT)
                    || cta.getCtaStatus().equals(CTAStatus.PAUSED)))
        .switchIfEmpty(
            Single.defer(
                () -> Single.error(new DefinedException(ErrorEntity.INVALID_STATUS_UPDATE))))
        .filter(
            ctaDetails ->
                (ctaDetails.getStartTime() != null
                    && ctaDetails.getStartTime() >= System.currentTimeMillis()))
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.INVALID_START_TIME))))
        .flatMapCompletable(
            it -> ctaRepository.update(it.getId(), it.getGenerationId(), CTAStatus.SCHEDULED))
        .doOnComplete(() -> log.info("CTA {} scheduled", id))
        .doOnError(error -> log.error("Error scheduling CTA {} ", id, error));
  }

  @Override
  public Completable updateStatusToConcluded(String tenantId, Long id) {
    return ctaRepository
        .find(tenantId, id)
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA))))
        .filter(
            cta ->
                cta.getCtaStatus().equals(CTAStatus.PAUSED)
                    || cta.getCtaStatus().equals(CTAStatus.LIVE))
        .switchIfEmpty(
            Single.defer(
                () -> Single.error(new DefinedException(ErrorEntity.INVALID_STATUS_UPDATE))))
        .flatMapCompletable(
            it -> {
              if (it.getStartTime() != null) {
                return ctaRepository
                    .terminateOrConclude(
                        id, CTAStatus.CONCLUDED, it.getStartTime(), System.currentTimeMillis())
                    .doOnComplete(() -> log.info("CTA {} concluded", id))
                    .doOnError(error -> log.error("Error concluding CTA {} ", id, error));
              } else {
                return ctaRepository
                    .terminateOrConclude(id, CTAStatus.CONCLUDED, System.currentTimeMillis())
                    .doOnComplete(() -> log.info("CTA {} concluded", id))
                    .doOnError(error -> log.error("Error concluding CTA {} ", id, error));
              }
            });
  }

  @Override
  public Completable updateStatusToTerminated(String tenantId, Long id) {
    return ctaRepository
        .find(tenantId, id)
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA))))
        .filter(
            cta ->
                cta.getCtaStatus().equals(CTAStatus.PAUSED)
                    || cta.getCtaStatus().equals(CTAStatus.LIVE)
                    || cta.getCtaStatus().equals(CTAStatus.DRAFT))
        .switchIfEmpty(
            Single.defer(
                () -> Single.error(new DefinedException(ErrorEntity.INVALID_STATUS_UPDATE))))
        .flatMapCompletable(
            it -> {
              if (it.getStartTime() != null) {
                return ctaRepository
                    .terminateOrConclude(
                        id, CTAStatus.TERMINATED, it.getStartTime(), System.currentTimeMillis())
                    .doOnComplete(() -> log.info("CTA {} terminated", id))
                    .doOnError(error -> log.error("Error terminating CTA {} ", id, error));
              } else {
                return ctaRepository
                    .terminateOrConclude(id, CTAStatus.TERMINATED, System.currentTimeMillis())
                    .doOnComplete(() -> log.info("CTA {} terminated", id))
                    .doOnError(error -> log.error("Error terminating CTA {} ", id, error));
              }
            });
  }

  @Override
  public Completable createNudge(String tenantId, Nudge template) {
    return nudgeRepository.create(tenantId, template);
  }

  @Override
  public Completable updateNudge(String tenantId, Nudge template) {
    return nudgeRepository.update(tenantId, template);
  }

  @Override
  public Single<FilterResponse> findFilters(String tenantId) {
    return ctaRepository.findFilters(tenantId).switchIfEmpty(Single.just(new FilterResponse()));
  }

  @Override
  public void activateScheduledCTA() {
    // TODO add logs, add dd metrics
    ctaRepository
        .findAllWithStatusScheduled()
        .flatMapObservable(map -> Observable.fromIterable(map.entrySet()))
        .filter(
            entry ->
                (entry.getValue().getStartTime() != null
                    && entry.getValue().getStartTime() < System.currentTimeMillis()))
        .doOnNext(
            entry ->
                ctaRepository
                    .update(
                        Long.parseLong(((Object) entry.getKey()).toString()),
                        entry.getValue().getGenerationId(),
                        CTAStatus.LIVE)
                    .subscribe(
                        () -> {
                          log.info("CTA activated: {}", entry.getKey());
                        },
                        error -> {
                          log.error("Error activating CTA: {}", entry.getKey(), error);
                        }))
        .doOnSubscribe(
            ignored -> {
              log.info("Activating Scheduled CTAs...");
            })
        .subscribe(
            ignored -> {},
            error -> {
              log.error("Error activating CTAs", error);
            },
            () -> {
              log.info("Scheduled CTAs activated...");
            });
  }

  @Override
  public void terminateExpiredCTA() {
    // TODO add logs, add dd metrics
    Single.zip(
            ctaRepository.findAllIdsWithStatusLive(),
            ctaRepository.findAllIdsWithStatusPaused(),
            (live, paused) ->
                new HashMap<Long, ActiveCTA>() {
                  {
                    putAll(live);
                    putAll(paused);
                  }
                })
        .flatMapObservable(map -> Observable.fromIterable(map.entrySet()))
        .filter(
            entry ->
                (entry.getValue().getEndTime() != null
                    && entry.getValue().getEndTime() < System.currentTimeMillis()))
        .doOnNext(
            entry ->
                ctaRepository
                    .update(
                        Long.parseLong(((Object) entry.getKey()).toString()),
                        entry.getValue().getGenerationId(),
                        CTAStatus.CONCLUDED)
                    .subscribe(
                        () -> {
                          log.info("CTA terminated: {}", entry.getKey());
                        },
                        error -> {
                          log.error("Error terminating CTA: {}", entry.getKey(), error);
                        }))
        .doOnSubscribe(
            ignored -> {
              log.info("Terminating Expired CTAs...");
            })
        .subscribe(
            ignored -> {},
            error -> {
              log.error("Error terminating CTAs", error);
            },
            () -> {
              log.info("Expired CTAs terminated...");
            });
  }
}

