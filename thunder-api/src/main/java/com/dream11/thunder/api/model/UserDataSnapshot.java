package com.dream11.thunder.api.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataSnapshot {

  private Map<Long, StateMachineSnapshot> stateMachines;
  private Map<String, BehaviourTagSnapshot> behaviourTags;
}
