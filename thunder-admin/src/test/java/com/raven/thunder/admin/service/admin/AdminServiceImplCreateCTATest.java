package com.raven.thunder.admin.service.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.raven.thunder.admin.exception.DefinedException;
import com.raven.thunder.admin.io.request.CTARequest;
import com.raven.thunder.admin.io.request.RuleRequest;
import com.raven.thunder.admin.service.AdminService;
import com.raven.thunder.core.dao.CTARepository;
import com.raven.thunder.core.dao.NudgePreviewRepository;
import com.raven.thunder.core.io.response.FilterResponse;
import com.raven.thunder.core.model.CohortEligibility;
import com.raven.thunder.core.model.Frequency;
import com.raven.thunder.core.model.rule.LifespanFrequency;
import com.raven.thunder.core.model.rule.SessionFrequency;
import com.raven.thunder.core.model.rule.StateTransitionCondition;
import com.raven.thunder.core.model.rule.WindowFrequency;
import com.raven.thunder.core.model.rule.WindowFrequencyUnit;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplCreateCTATest {

  @Mock private CTARepository ctaRepository;
  @Mock private NudgePreviewRepository nudgePreviewRepository;

  @InjectMocks private AdminServiceImpl adminService;

  private CTARequest buildValidCTARequest(String name) {
    // CohortEligibility
    CohortEligibility eligibility = new CohortEligibility(List.of("cohortA"), List.of());
    // Transitions: stateA -> eventX -> [condition]
    StateTransitionCondition.Filters filters = new StateTransitionCondition.Filters();
    filters.setOperator("AND");
    filters.setFilter(List.of("any"));
    StateTransitionCondition condition = new StateTransitionCondition();
    condition.setTransitionTo("stateB");
    condition.setFilters(filters);
    Map<String, List<StateTransitionCondition>> eventMap = new HashMap<>();
    eventMap.put("eventX", List.of(condition));
    Map<String, Map<String, List<StateTransitionCondition>>> stateTransition = new HashMap<>();
    stateTransition.put("stateA", eventMap);

    // Frequency
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

    CTARequest req = new CTARequest();
    req.setName(name);
    req.setDescription("desc");
    req.setTags(List.of("tag1"));
    req.setTeam("teamA");
    req.setStartTime(null);
    req.setEndTime(null);
    req.setRule(rule);
    return req;
  }

  @Test
  void createCTA_success_generatesId_createsAndUpdatesFilters() {
    String tenantId = "tenant-1";
    String user = "user@x";
    CTARequest req = buildValidCTARequest("Welcome");

    // No duplicate names
    when(ctaRepository.findFilters(tenantId))
        .thenReturn(Maybe.just(new FilterResponse(List.of(), List.of(), List.of(), List.of())));

    // Id generation + create
    when(ctaRepository.generatedIncrementId(tenantId)).thenReturn(Single.just(123L));
    when(ctaRepository.create(eq(tenantId), any())).thenReturn(Completable.complete());
    when(ctaRepository.updateFilters(
            eq(tenantId), anyList(), anyString(), anyString(), anyString()))
        .thenReturn(Completable.complete());

    AdminService service = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);
    Long id = service.createCTA(tenantId, req, user).blockingGet();

    assertThat(id).isEqualTo(123L);
    verify(ctaRepository, times(1)).generatedIncrementId(tenantId);
    verify(ctaRepository, times(1)).create(eq(tenantId), any());
    // updateFilters is triggered on success (fire-and-forget)
    verify(ctaRepository, atLeastOnce())
        .updateFilters(
            eq(tenantId), eq(req.getTags()), eq(req.getTeam()), eq(req.getName()), eq(user));
  }

  @Test
  void createCTA_failsOnDuplicateName() {
    String tenantId = "tenant-1";
    String user = "user@x";
    CTARequest req = buildValidCTARequest("Duplicate");

    when(ctaRepository.findFilters(tenantId))
        .thenReturn(
            Maybe.just(new FilterResponse(List.of("Duplicate"), List.of(), List.of(), List.of())));

    AdminService service = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);

    assertThrows(
        DefinedException.class, () -> service.createCTA(tenantId, req, user).blockingGet());
    verify(ctaRepository, never()).generatedIncrementId(anyString());
    verify(ctaRepository, never()).create(anyString(), any());
  }
}
