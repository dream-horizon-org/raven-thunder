package com.raven.thunder.admin.service.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.raven.thunder.admin.exception.DefinedException;
import com.raven.thunder.admin.service.AdminService;
import com.raven.thunder.core.dao.CTARepository;
import com.raven.thunder.core.dao.NudgePreviewRepository;
import com.raven.thunder.core.dao.cta.CTADetails;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.CTAStatus;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplStatusTest {

  @Mock private CTARepository ctaRepository;
  @Mock private NudgePreviewRepository nudgePreviewRepository;
  @InjectMocks private AdminServiceImpl adminService;

  @Test
  void updateStatusToLive_fromDraft_setsTimesAndCallsUpdate() {
    String tenantId = "tenant-1";
    Long id = 100L;
    CTA draft = new CTA();
    draft.setId(id);
    draft.setCtaStatus(CTAStatus.DRAFT);
    draft.setEndTime(null); // triggers default end time logic

    when(ctaRepository.find(tenantId, id)).thenReturn(Maybe.just(draft));
    ArgumentCaptor<Long> startCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Long> endCaptor = ArgumentCaptor.forClass(Long.class);
    when(ctaRepository.update(
            eq(id), eq(CTAStatus.LIVE), startCaptor.capture(), endCaptor.capture()))
        .thenReturn(Completable.complete());

    AdminService svc = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);
    svc.updateStatusToLive(tenantId, id).test().assertComplete();

    verify(ctaRepository, times(1)).find(tenantId, id);
    verify(ctaRepository, times(1)).update(eq(id), eq(CTAStatus.LIVE), anyLong(), anyLong());
    long start = startCaptor.getValue();
    long end = endCaptor.getValue();
    // end should be after start; exact offset is implementation-specific, so assert ordering
    assertEquals(true, end > start, "endTime must be after startTime");
  }

  @Test
  void updateStatusToLive_invalidCurrentStatus_throws() {
    String tenantId = "tenant-1";
    Long id = 200L;
    CTA live = new CTA();
    live.setId(id);
    live.setCtaStatus(CTAStatus.LIVE); // not allowed to transition to LIVE

    when(ctaRepository.find(tenantId, id)).thenReturn(Maybe.just(live));

    AdminService svc = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);
    assertThrows(
        DefinedException.class, () -> svc.updateStatusToLive(tenantId, id).blockingAwait());
    verify(ctaRepository, never()).update(eq(id), eq(CTAStatus.LIVE), anyLong(), anyLong());
  }

  @Test
  void updateStatusToScheduled_fromDraft_withFutureStart_updatesWithGeneration() {
    String tenantId = "tenant-1";
    Long id = 300L;
    int generation = 5;
    CTADetails details = new CTADetails();
    details.setId(id);
    details.setGenerationId(generation);
    details.setCtaStatus(CTAStatus.DRAFT);
    details.setStartTime(System.currentTimeMillis() + 60_000); // in future

    when(ctaRepository.findWithGeneration(tenantId, id)).thenReturn(Maybe.just(details));
    when(ctaRepository.update(id, generation, CTAStatus.SCHEDULED))
        .thenReturn(Completable.complete());

    AdminService svc = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);
    svc.updateStatusToScheduled(tenantId, id).test().assertComplete();

    verify(ctaRepository, times(1)).findWithGeneration(tenantId, id);
    verify(ctaRepository, times(1)).update(id, generation, CTAStatus.SCHEDULED);
  }

  @Test
  void updateStatusToScheduled_invalidStartTime_throws() {
    String tenantId = "tenant-1";
    Long id = 301L;
    CTADetails details = new CTADetails();
    details.setId(id);
    details.setGenerationId(1);
    details.setCtaStatus(CTAStatus.DRAFT);
    details.setStartTime(System.currentTimeMillis() - 60_000); // past

    when(ctaRepository.findWithGeneration(tenantId, id)).thenReturn(Maybe.just(details));

    AdminService svc = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);
    assertThrows(
        DefinedException.class, () -> svc.updateStatusToScheduled(tenantId, id).blockingAwait());
    verify(ctaRepository, never()).update(eq(id), anyInt(), eq(CTAStatus.SCHEDULED));
  }

  @Test
  void updateStatusToPaused_fromLive_updatesStatus() {
    String tenantId = "tenant-1";
    Long id = 400L;
    CTA live = new CTA();
    live.setId(id);
    live.setCtaStatus(CTAStatus.LIVE);

    when(ctaRepository.find(tenantId, id)).thenReturn(Maybe.just(live));
    when(ctaRepository.update(id, CTAStatus.PAUSED)).thenReturn(Completable.complete());

    AdminService svc = new AdminServiceImpl(ctaRepository, nudgePreviewRepository);
    svc.updateStatusToPaused(tenantId, id).test().assertComplete();

    verify(ctaRepository, times(1)).update(id, CTAStatus.PAUSED);
  }
}
