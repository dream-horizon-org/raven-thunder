package com.dream11.thunder.admin.service.admin;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.admin.io.request.RuleRequest;
import com.dream11.thunder.admin.service.AdminService;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.dao.NudgePreviewRepository;
import com.dream11.thunder.core.dao.cta.CTADetails;
import com.dream11.thunder.core.model.CTAStatus;
import com.dream11.thunder.core.model.CohortEligibility;
import com.dream11.thunder.core.model.Frequency;
import com.dream11.thunder.core.model.rule.LifespanFrequency;
import com.dream11.thunder.core.model.rule.SessionFrequency;
import com.dream11.thunder.core.model.rule.StateTransitionCondition;
import com.dream11.thunder.core.model.rule.WindowFrequency;
import com.dream11.thunder.core.model.rule.WindowFrequencyUnit;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplUpdateCTATest {

  @Mock private CTARepository ctaRepository;
  @Mock private NudgePreviewRepository nudgePreviewRepository;
  @InjectMocks private AdminServiceImpl adminService;

  private CTAUpdateRequest buildUpdateRequest() {
    // minimal valid RuleRequest inside CTAUpdateRequest
    CohortEligibility eligibility = new CohortEligibility(List.of("cohortA"), List.of());
    StateTransitionCondition cond = new StateTransitionCondition();
    StateTransitionCondition.Filters filters = new StateTransitionCondition.Filters();
    filters.setOperator("AND");
    filters.setFilter(List.of("x"));
    cond.setTransitionTo("stateB");
    cond.setFilters(filters);
    Map<String, List<StateTransitionCondition>> eventMap = new HashMap<>();
    eventMap.put("eventX", List.of(cond));
    Map<String, Map<String, List<StateTransitionCondition>>> stateTransition = new HashMap<>();
    stateTransition.put("stateA", eventMap);
    SessionFrequency session = new SessionFrequency();
    session.setLimit(1);
    WindowFrequency window = new WindowFrequency();
    window.setLimit(1);
    window.setUnit(WindowFrequencyUnit.days);
    window.setValue(1);
    LifespanFrequency lifespan = new LifespanFrequency();
    lifespan.setLimit(10);
    Frequency frequency = new Frequency();
    frequency.setSession(session);
    frequency.setWindow(window);
    frequency.setLifeSpan(lifespan);
    RuleRequest rule =
        new RuleRequest(
            eligibility,
            Map.of("stateA", "actionA"),
            List.of(),
            false,
            List.of(),
            stateTransition,
            null,
            1,
            86_400_000L,
            List.of(Map.of()),
            frequency);
    CTAUpdateRequest req = new CTAUpdateRequest();
    req.setDescription("new-desc");
    req.setTags(List.of("t1", "t2"));
    req.setTeam("teamX");
    req.setStartTime(null);
    req.setEndTime(null);
    req.setRule(rule);
    return req;
  }

  private CTADetails buildDetails(long id, int generation, CTAStatus status) {
    CTADetails d = new CTADetails();
    d.setId(id);
    d.setGenerationId(generation);
    d.setCtaStatus(status);
    d.setName("OldName");
    d.setBehaviourTags(List.of());
    d.setCreatedAt(System.currentTimeMillis() - 1000);
    d.setCreatedBy("creator");
    d.setTenantId("tenant-1");
    return d;
  }

  @Test
  void updateCTA_success_updatesAndFilters() {
    String tenantId = "tenant-1";
    long ctaId = 55L;
    int gen = 3;
    CTAUpdateRequest req = buildUpdateRequest();
    CTADetails details = buildDetails(ctaId, gen, CTAStatus.DRAFT);

    when(ctaRepository.findWithGeneration(tenantId, ctaId)).thenReturn(Maybe.just(details));
    when(ctaRepository.update(any(), eq(gen))).thenReturn(Completable.complete());
    when(ctaRepository.updateFilters(
            eq(tenantId), anyList(), anyString(), anyString(), anyString()))
        .thenReturn(Completable.complete());

    AdminService svc = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);
    svc.updateCTA(tenantId, req, ctaId, "user@x").test().assertComplete();

    verify(ctaRepository, times(1)).findWithGeneration(tenantId, ctaId);
    verify(ctaRepository, times(1)).update(any(), eq(gen));
    verify(ctaRepository, atLeastOnce())
        .updateFilters(
            eq(tenantId), eq(req.getTags()), eq(req.getTeam()), anyString(), eq("user@x"));
  }

  @Test
  void updateCTA_repoError_isWrappedIntoDefinedException() {
    String tenantId = "tenant-1";
    long ctaId = 56L;
    int gen = 2;
    CTAUpdateRequest req = buildUpdateRequest();
    CTADetails details = buildDetails(ctaId, gen, CTAStatus.DRAFT);

    when(ctaRepository.findWithGeneration(tenantId, ctaId)).thenReturn(Maybe.just(details));
    when(ctaRepository.update(any(), eq(gen)))
        .thenReturn(Completable.error(new RuntimeException("db")));

    AdminService svc = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);
    // blockingAwait throws on error; ensure it's our domain exception
    org.junit.jupiter.api.Assertions.assertThrows(
        DefinedException.class,
        () -> svc.updateCTA(tenantId, req, ctaId, "user@x").blockingAwait());
  }
}
