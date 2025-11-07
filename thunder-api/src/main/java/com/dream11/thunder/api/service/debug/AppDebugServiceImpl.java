package com.dream11.thunder.api.service.debug;

import com.dream11.thunder.api.dao.StateMachineRepository;
import com.dream11.thunder.api.model.UserDataSnapshot;
import com.dream11.thunder.api.service.AppDebugService;
import com.dream11.thunder.api.service.StaticDataCache;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.model.CTA;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import java.util.stream.Collectors;

public class AppDebugServiceImpl implements AppDebugService {

  private final CTARepository ctaRepository;
  private final StateMachineRepository stateMachineRepository;
  private final StaticDataCache cache;

  @Inject
  public AppDebugServiceImpl(
      CTARepository ctaRepository,
      StateMachineRepository stateMachineRepository,
      StaticDataCache cache) {
    this.ctaRepository = ctaRepository;
    this.stateMachineRepository = stateMachineRepository;
    this.cache = cache;
  }

  @Override
  public Single<Map<Long, CTA>> findAllActiveCTA(String tenantId, Boolean cache) {
    final boolean useCache = Boolean.TRUE.equals(cache);

    Single<Map<Long, CTA>> src =
        useCache
            ? Single.just(this.cache.findAllActiveCTA())
            : ctaRepository.findAllWithStatusActive();

    // filter to the caller’s tenant before returning
    return src.map(
        all ->
            all.entrySet().stream()
                .filter(e -> tenantId.equals(e.getValue().getTenantId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  @Override
  public Maybe<CTA> findCTA(String tenantId, Long id) {
    return ctaRepository.find(tenantId, id);
  }

  @Override
  public Maybe<UserDataSnapshot> findStateMachine(String tenantId, Long userId) {
    return stateMachineRepository.find(tenantId, userId);
  }
}
