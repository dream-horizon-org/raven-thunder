package com.raven.thunder.admin.service.admin;

import com.raven.thunder.admin.exception.DefinedException;
import com.raven.thunder.admin.exception.ErrorEntity;
import com.raven.thunder.admin.io.request.CTARequest;
import com.raven.thunder.admin.io.request.CTAUpdateRequest;
import com.raven.thunder.admin.io.response.CTAListResponse;
import com.raven.thunder.admin.model.FilterProps;
import com.raven.thunder.admin.service.AdminService;
import com.raven.thunder.admin.service.filters.CTAFilters;
import com.raven.thunder.admin.util.CTAPaginationHelper;
import com.raven.thunder.admin.util.CTAStatusValidator;
import com.raven.thunder.admin.util.Constants;
import com.raven.thunder.core.dao.CTARepository;
import com.raven.thunder.core.dao.NudgePreviewRepository;
import com.raven.thunder.core.dao.cta.ActiveCTA;
import com.raven.thunder.core.dao.cta.ScheduledCTA;
import com.raven.thunder.core.error.ServiceError;
import com.raven.thunder.core.exception.ThunderException;
import com.raven.thunder.core.io.response.FilterResponse;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.CTAStatus;
import com.raven.thunder.core.model.NudgePreview;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminServiceImpl implements AdminService {

  private final CTARepository ctaRepository;
  private final NudgePreviewRepository nudgePreviewRepository;
  private final CreateCTAMapper createCtaMapper = new CreateCTAMapper();
  private final CTAUpdateValidator ctaUpdateValidator = new CTAUpdateValidator();

  @Inject
  public AdminServiceImpl(
      CTARepository ctaRepository, NudgePreviewRepository nudgePreviewRepository) {
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
              ctaListResponse.setStatusWiseCount(CTAPaginationHelper.countByStatus(ctas));
              ctas = CTAPaginationHelper.filterByStatus(ctas, filterProps.getStatus());
              int totalEntries = ctas.size();
              ctas = CTAPaginationHelper.paginate(ctas, pageNumber, pageSize);
              ctaListResponse.setCtas(ctas);
              ctaListResponse.setPageNumber(pageNumber);
              ctaListResponse.setPageSize(pageSize);
              ctaListResponse.setTotalPages(
                  CTAPaginationHelper.calculateTotalPages(totalEntries, pageSize));
              ctaListResponse.setTotalEntries(totalEntries);
              return ctaListResponse;
            });
  }

  @Override
  public Completable updateStatusToPaused(String tenantId, Long id) {
    return findAndValidateCTA(tenantId, id, CTAStatusValidator::canPause)
        .flatMapCompletable(cta -> ctaRepository.update(id, CTAStatus.PAUSED));
  }

  @Override
  public Completable updateStatusToLive(String tenantId, Long id) {
    return findAndValidateCTA(tenantId, id, CTAStatusValidator::canMakeLive)
        .flatMapCompletable(
            cta -> {
              long startTime = System.currentTimeMillis();
              long endTime =
                  cta.getEndTime() == null ? startTime + Constants.THREE_YEARS : cta.getEndTime();
              return ctaRepository
                  .update(id, CTAStatus.LIVE, startTime, endTime)
                  .doOnComplete(() -> log.info("CTA {} made live", id))
                  .doOnError(error -> log.error("Error making CTA {} live ", id, error));
            });
  }

  @Override
  public Completable updateStatusToScheduled(String tenantId, Long id) {
    return ctaRepository
        .findWithGeneration(tenantId, id)
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA))))
        .filter(
            ctaDetails ->
                ctaDetails.getCtaStatus() == CTAStatus.DRAFT
                    || ctaDetails.getCtaStatus() == CTAStatus.PAUSED)
        .switchIfEmpty(
            Single.defer(
                () -> Single.error(new DefinedException(ErrorEntity.INVALID_STATUS_UPDATE))))
        .filter(
            ctaDetails ->
                CTAStatusValidator.isValidStartTimeForScheduling(ctaDetails.getStartTime()))
        .switchIfEmpty(
            Single.defer(() -> Single.error(new DefinedException(ErrorEntity.INVALID_START_TIME))))
        .flatMapCompletable(
            ctaDetails ->
                ctaRepository
                    .update(ctaDetails.getId(), ctaDetails.getGenerationId(), CTAStatus.SCHEDULED)
                    .doOnComplete(() -> log.info("CTA {} scheduled", id))
                    .doOnError(error -> log.error("Error scheduling CTA {} ", id, error)));
  }

  @Override
  public Completable updateStatusToConcluded(String tenantId, Long id) {
    return findAndValidateCTA(tenantId, id, CTAStatusValidator::canConclude)
        .flatMapCompletable(
            cta ->
                concludeCTA(id, cta)
                    .doOnComplete(() -> log.info("CTA {} concluded", id))
                    .doOnError(error -> log.error("Error concluding CTA {} ", id, error)));
  }

  @Override
  public Completable updateStatusToTerminated(String tenantId, Long id) {
    return findAndValidateCTA(tenantId, id, CTAStatusValidator::canTerminate)
        .flatMapCompletable(
            cta ->
                terminateCTA(id, cta)
                    .doOnComplete(() -> log.info("CTA {} terminated", id))
                    .doOnError(error -> log.error("Error terminating CTA {} ", id, error)));
  }

  @Override
  public Single<FilterResponse> findFilters(String tenantId) {
    return ctaRepository.findFilters(tenantId).switchIfEmpty(Single.just(new FilterResponse()));
  }

  @Override
  public void activateScheduledCTA() {
    log.info("Activating Scheduled CTAs...");
    ctaRepository
        .findAllWithStatusScheduled()
        .flatMapObservable(map -> Observable.fromIterable(map.entrySet()))
        .filter(this::isReadyToActivate)
        .doOnNext(this::activateCTA)
        .subscribe(
            ignored -> {},
            error -> log.error("Error activating CTAs", error),
            () -> log.info("Scheduled CTAs activation completed"));
  }

  @Override
  public void terminateExpiredCTA() {
    log.info("Terminating Expired CTAs...");
    Single.zip(
            ctaRepository.findAllIdsWithStatusLive(),
            ctaRepository.findAllIdsWithStatusPaused(),
            this::mergeActiveCTAs)
        .flatMapObservable(map -> Observable.fromIterable(map.entrySet()))
        .filter(this::isExpired)
        .doOnNext(this::terminateExpiredCTA)
        .subscribe(
            ignored -> {},
            error -> log.error("Error terminating CTAs", error),
            () -> log.info("Expired CTAs termination completed"));
  }

  private Single<CTA> findAndValidateCTA(
      String tenantId, Long id, java.util.function.Predicate<CTA> validator) {
    return ctaRepository
        .find(tenantId, id)
        .toSingle()
        .onErrorResumeNext(throwable -> Single.error(new DefinedException(ErrorEntity.NO_SUCH_CTA)))
        .flatMap(
            cta ->
                validator.test(cta)
                    ? Single.just(cta)
                    : Single.error(new DefinedException(ErrorEntity.INVALID_STATUS_UPDATE)));
  }

  private Completable concludeCTA(Long id, CTA cta) {
    long endTime = System.currentTimeMillis();
    if (cta.getStartTime() != null) {
      return ctaRepository.terminateOrConclude(
          id, CTAStatus.CONCLUDED, cta.getStartTime(), endTime);
    } else {
      return ctaRepository.terminateOrConclude(id, CTAStatus.CONCLUDED, endTime);
    }
  }

  private Completable terminateCTA(Long id, CTA cta) {
    long endTime = System.currentTimeMillis();
    if (cta.getStartTime() != null) {
      return ctaRepository.terminateOrConclude(
          id, CTAStatus.TERMINATED, cta.getStartTime(), endTime);
    } else {
      return ctaRepository.terminateOrConclude(id, CTAStatus.TERMINATED, endTime);
    }
  }

  private boolean isReadyToActivate(Map.Entry<Long, ScheduledCTA> entry) {
    Long startTime = entry.getValue().getStartTime();
    return startTime != null && startTime < System.currentTimeMillis();
  }

  private void activateCTA(Map.Entry<Long, ScheduledCTA> entry) {
    Long ctaId = entry.getKey();
    ScheduledCTA scheduledCTA = entry.getValue();
    ctaRepository
        .update(ctaId, scheduledCTA.getGenerationId(), CTAStatus.LIVE)
        .subscribe(
            () -> log.info("CTA activated: {}", ctaId),
            error -> log.error("Error activating CTA: {}", ctaId, error));
  }

  private Map<Long, ActiveCTA> mergeActiveCTAs(
      Map<Long, ActiveCTA> live, Map<Long, ActiveCTA> paused) {
    Map<Long, ActiveCTA> merged = new HashMap<>(live);
    merged.putAll(paused);
    return merged;
  }

  private boolean isExpired(Map.Entry<Long, ActiveCTA> entry) {
    Long endTime = entry.getValue().getEndTime();
    return endTime != null && endTime < System.currentTimeMillis();
  }

  private void terminateExpiredCTA(Map.Entry<Long, ActiveCTA> entry) {
    Long ctaId = entry.getKey();
    ActiveCTA activeCTA = entry.getValue();
    ctaRepository
        .update(ctaId, activeCTA.getGenerationId(), CTAStatus.CONCLUDED)
        .subscribe(
            () -> log.info("CTA terminated: {}", ctaId),
            error -> log.error("Error terminating CTA: {}", ctaId, error));
  }
}
