package com.raven.thunder.api.service.sdk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.raven.thunder.api.dao.StateMachineRepository;
import com.raven.thunder.api.io.request.CTASnapshotRequest;
import com.raven.thunder.api.io.response.CTAResponse;
import com.raven.thunder.api.model.StateMachine;
import com.raven.thunder.api.model.StateMachineSnapshot;
import com.raven.thunder.api.model.UserDataSnapshot;
import com.raven.thunder.api.service.StaticDataCache;
import com.raven.thunder.api.service.UserCohortsClient;
import com.raven.thunder.core.dao.NudgePreviewRepository;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.CohortEligibility;
import com.raven.thunder.core.model.rule.Rule;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SdkServiceImplAppLaunchTest {

  @Mock private UserCohortsClient userCohortsClient;
  @Mock private StateMachineRepository stateMachineRepository;
  @Mock private StaticDataCache cache;
  @Mock private NudgePreviewRepository nudgePreviewRepository;
  @InjectMocks private SdkServiceImpl sdkService;

  private CTA buildCTA(long id, String tenantId, Set<String> include, Set<String> exclude) {
    Rule rule =
        new Rule(
            new CohortEligibility(List.copyOf(include), List.copyOf(exclude)),
            Map.of("stateA", "actionA"),
            List.of(),
            false,
            List.of(),
            Map.of(), // stateTransition not needed for this test
            null,
            1,
            null,
            null,
            List.of(Map.of()),
            new com.raven.thunder.core.model.Frequency());
    CTA cta = new CTA();
    cta.setId(id);
    cta.setRule(rule);
    cta.setTenantId(tenantId);
    cta.setBehaviourTags(List.of());
    return cta;
  }

  @Test
  void appLaunch_returnsEligibleCTA_withoutUpsertWhenSnapshotUnchanged() {
    String tenantId = "tenant-1";
    long userId = 77L;

    when(userCohortsClient.findAllCohorts(userId)).thenReturn(Single.just(Set.of("includeA")));
    Map<Long, CTA> active = new HashMap<>();
    active.put(99L, buildCTA(99L, tenantId, Set.of("includeA"), Set.of()));
    when(cache.findAllActiveCTA()).thenReturn(active);
    when(cache.findAllPausedCTA()).thenReturn(Map.of());
    when(cache.findAllBehaviourTags()).thenReturn(Map.of());

    when(stateMachineRepository.find(tenantId, userId))
        .thenReturn(Maybe.just(new UserDataSnapshot(new HashMap<>(), new HashMap<>())));

    CTAResponse response = sdkService.appLaunch(tenantId, userId, null).blockingGet();

    assertThat(response).isNotNull();
    assertThat(response.getCtas()).hasSize(1);
    assertThat(response.getCtas().get(0).getCtaId()).isEqualTo("99");
    verify(stateMachineRepository, never()).upsert(eq(tenantId), eq(userId), any());
  }

  @Test
  void merge_mergesDeltaSnapshot_andUpserts() {
    String tenantId = "tenant-1";
    long userId = 88L;
    when(stateMachineRepository.find(tenantId, userId))
        .thenReturn(Maybe.just(new UserDataSnapshot(new HashMap<>(), new HashMap<>())));
    when(stateMachineRepository.upsert(eq(tenantId), eq(userId), any()))
        .thenReturn(Single.just(true));

    StateMachine sm = new StateMachine();
    sm.setCurrentState("S1");
    sm.setCreatedAt(System.currentTimeMillis());
    sm.setLastTransitionAt(System.currentTimeMillis());

    StateMachineSnapshot sms = new StateMachineSnapshot();
    sms.setCtaId("5");
    sms.setActiveStateMachines(Map.of("g1", sm));
    CTASnapshotRequest delta = new CTASnapshotRequest();
    delta.setCtas(List.of(sms));

    Boolean result = sdkService.merge(tenantId, userId, delta).blockingGet();
    assertThat(result).isTrue();
    verify(stateMachineRepository, times(1)).upsert(eq(tenantId), eq(userId), any());
  }
}
