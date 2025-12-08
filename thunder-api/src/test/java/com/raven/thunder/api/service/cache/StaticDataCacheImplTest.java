package com.raven.thunder.api.service.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.raven.thunder.core.dao.BehaviourTagsRepository;
import com.raven.thunder.core.dao.CTARepository;
import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.model.CTA;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaticDataCacheImplTest {

  @Mock private CTARepository ctaRepository;
  @Mock private BehaviourTagsRepository behaviourTagsRepository;

  @InjectMocks private StaticDataCacheImpl cache;

  @Test
  void initiateCache_loadsAllDatasets_andFindReturnsCopies() {
    // active
    CTA active = new CTA();
    active.setId(1L);
    // paused
    CTA paused = new CTA();
    paused.setId(2L);
    // tags
    BehaviourTag tag = new BehaviourTag();
    tag.setName("bt1");

    when(ctaRepository.findAllWithStatusActive()).thenReturn(Single.just(Map.of(1L, active)));
    when(ctaRepository.findAllWithStatusPaused()).thenReturn(Single.just(Map.of(2L, paused)));
    when(behaviourTagsRepository.findAll()).thenReturn(Single.just(Map.of("bt1", tag)));

    cache.initiateCache().join();

    Map<Long, CTA> activeOut = cache.findAllActiveCTA();
    Map<Long, CTA> pausedOut = cache.findAllPausedCTA();
    Map<String, BehaviourTag> tagsOut = cache.findAllBehaviourTags();

    assertThat(activeOut).containsOnlyKeys(1L);
    assertThat(pausedOut).containsOnlyKeys(2L);
    assertThat(tagsOut).containsOnlyKeys("bt1");

    // Ensure copies returned (mutation does not affect cache)
    activeOut.clear();
    assertThat(cache.findAllActiveCTA()).containsOnlyKeys(1L);
  }
}
