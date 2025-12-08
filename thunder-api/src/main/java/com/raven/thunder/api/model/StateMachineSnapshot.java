package com.raven.thunder.api.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateMachineSnapshot {
  private String ctaId;
  private Map<String, StateMachine> activeStateMachines;
  private List<Long> resetAt;
  private List<Long> actionDoneAt;
}
