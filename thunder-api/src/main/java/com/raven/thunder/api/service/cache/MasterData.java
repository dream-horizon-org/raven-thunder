package com.raven.thunder.api.service.cache;

import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.TestCTA;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class MasterData {

  private Map<Long, CTA> activeCTACache;
  private Map<Long, CTA> pausedCTACache;
  private Map<String, BehaviourTag> behaviourTagCache;
  private Map<String, List<TestCTA>> testCTACache;
}
