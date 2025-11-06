package com.dream11.thunder.api.service.cache;

import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTA;
import java.util.Map;
import lombok.Data;

@Data
public class MasterData {

  private Map<Long, CTA> activeCTACache;
  private Map<Long, CTA> pausedCTACache;
  private Map<String, BehaviourTag> behaviourTagCache;
}
