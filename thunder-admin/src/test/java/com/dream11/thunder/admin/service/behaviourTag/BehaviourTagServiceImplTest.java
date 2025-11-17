package com.dream11.thunder.admin.service.behaviourTag;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.io.request.BehaviourTagCreateRequest;
import com.dream11.thunder.admin.io.request.BehaviourTagPutRequest;
import com.dream11.thunder.admin.service.BehaviourTagService;
import com.dream11.thunder.core.dao.BehaviourTagsRepository;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BehaviourTagServiceImplTest {

  @Mock private BehaviourTagsRepository behaviourTagsRepository;
  @Mock private CTARepository ctaRepository;
  @InjectMocks private BehaviourTagServiceImpl service;

  @Test
  void createBehaviourTag_rejectsLiveOrScheduledCTAs() {
    String tenantId = "tenant-1";
    String user = "user@x";

    BehaviourTagCreateRequest req = new BehaviourTagCreateRequest();
    req.setBehaviourTagName("bt1");
    req.setDescription("desc");
    req.setLinkedCtas(Set.of("10")); // link invalid CTA id

    // Active CTA map: CTA id 10 is LIVE
    CTA live = new CTA();
    live.setId(10L);
    live.setCtaStatus(CTAStatus.LIVE);
    Map<Long, CTA> ctas = new HashMap<>();
    ctas.put(10L, live);

    when(ctaRepository.findAll(tenantId)).thenReturn(Single.just(ctas));

    assertThrows(
        DefinedException.class,
        () -> service.createBehaviourTag(tenantId, user, req).blockingAwait());
    verify(behaviourTagsRepository, never()).create(anyString(), any());
  }

  @Test
  void updateBehaviourTag_unlinksRemoved_and_linksNew() {
    String tenantId = "tenant-1";
    String user = "user@x";
    String name = "bt1";

    // Existing BT linked to 1 and 2
    BehaviourTag existing = new BehaviourTag();
    existing.setName(name);
    existing.setLinkedCtas(new HashSet<>(Set.of("1", "2")));

    BehaviourTagPutRequest put = new BehaviourTagPutRequest();
    // Incoming wants 2 and 3 -> unlink 1, keep 2, link 3
    put.setLinkedCtas(new HashSet<>(Set.of("2", "3")));

    // ctaRepository.findAll used for validation: ensure statuses are allowed
    CTA c1 = new CTA(); c1.setId(1L); c1.setCtaStatus(CTAStatus.PAUSED);
    CTA c2 = new CTA(); c2.setId(2L); c2.setCtaStatus(CTAStatus.DRAFT);
    CTA c3 = new CTA(); c3.setId(3L); c3.setCtaStatus(CTAStatus.DRAFT);
    Map<Long, CTA> ctas = new HashMap<>();
    ctas.put(1L, c1); ctas.put(2L, c2); ctas.put(3L, c3);
    when(ctaRepository.findAll(tenantId)).thenReturn(Single.just(ctas));

    when(behaviourTagsRepository.find(tenantId, name)).thenReturn(Maybe.just(existing));
    when(behaviourTagsRepository.update(eq(tenantId), eq(name), any()))
        .thenReturn(Completable.complete());

    // For unlink/link calls
    when(ctaRepository.update(eq(1L), eq(List.of()))).thenReturn(Completable.complete());
    when(ctaRepository.update(eq(2L), eq(List.of(name)))).thenReturn(Completable.complete());
    when(ctaRepository.update(eq(3L), eq(List.of(name)))).thenReturn(Completable.complete());

    BehaviourTagService btService = new BehaviourTagServiceImpl(behaviourTagsRepository, ctaRepository);
    btService.updateBehaviourTag(tenantId, name, put, user).test().assertComplete();

    // unlink 1
    verify(ctaRepository, atLeastOnce()).update(1L, List.of());
    // link 2 and 3 (2 might be linked already by the main loop; implementation links incoming set)
    verify(ctaRepository, atLeastOnce()).update(2L, List.of(name));
    verify(ctaRepository, atLeastOnce()).update(3L, List.of(name));
    // BT updated
    verify(behaviourTagsRepository, times(1)).update(eq(tenantId), eq(name), any());
  }
}


