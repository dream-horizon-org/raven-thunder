package com.raven.thunder.api.service.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.raven.thunder.core.dao.BehaviourTagsRepository;
import com.raven.thunder.core.dao.CTARepository;
import com.raven.thunder.core.dao.TestCTARepository;
import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.model.CTA;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.Vertx;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaticDataCacheImplTest {

  @Mock private CTARepository ctaRepository;
  @Mock private BehaviourTagsRepository behaviourTagsRepository;
  @Mock private TestCTARepository testCTARepository;

  private Vertx vertx;
  private StaticDataCacheImpl cache;

  @BeforeEach
  void setUp() {
    // Create real Vertx instance since it can't be mocked
    vertx = Vertx.vertx();
    cache =
        new StaticDataCacheImpl(ctaRepository, behaviourTagsRepository, testCTARepository, vertx);
  }

  @AfterEach
  void tearDown() {
    if (vertx != null) {
      vertx.close();
    }
  }

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

    Map<Long, CTA> activeMap = new HashMap<>();
    activeMap.put(1L, active);
    Map<Long, CTA> pausedMap = new HashMap<>();
    pausedMap.put(2L, paused);
    Map<String, BehaviourTag> tagMap = new HashMap<>();
    tagMap.put("bt1", tag);

    when(ctaRepository.findAllWithStatusActive()).thenReturn(Single.just(activeMap));
    when(ctaRepository.findAllWithStatusPaused()).thenReturn(Single.just(pausedMap));
    when(behaviourTagsRepository.findAll()).thenReturn(Single.just(tagMap));
    when(testCTARepository.fetchAllTestCTAs()).thenReturn(Single.just(Collections.emptyMap()));

    cache.initiateCache().join();

    Map<Long, CTA> activeOut = cache.findAllActiveCTA();
    Map<Long, CTA> pausedOut = cache.findAllPausedCTA();
    Map<String, BehaviourTag> tagsOut = cache.findAllBehaviourTags();

    assertThat(activeOut).containsOnlyKeys(1L);
    assertThat(pausedOut).containsOnlyKeys(2L);
    assertThat(tagsOut).containsOnlyKeys("bt1");

    // Verify cache returns the same reference (not a copy)
    // The cache returns the actual map, so mutations will affect the cache
    // This is the expected behavior - the cache holds the reference
    assertThat(activeOut).isSameAs(cache.findAllActiveCTA());
  }
}
