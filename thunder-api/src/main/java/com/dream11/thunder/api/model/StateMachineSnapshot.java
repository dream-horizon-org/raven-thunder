package com.dream11.thunder.api.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class StateMachineSnapshot {
  private String ctaId;
  private Map<String, StateMachine> activeStateMachines;
  private List<Long> resetAt;
  private List<Long> actionDoneAt;
}
