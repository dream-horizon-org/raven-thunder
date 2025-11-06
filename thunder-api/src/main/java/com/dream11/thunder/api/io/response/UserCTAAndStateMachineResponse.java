package com.dream11.thunder.api.io.response;

import com.dream11.thunder.api.model.StateMachine;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCTAAndStateMachineResponse {
  private String ctaId;
  private RuleResponse rule;
  private Map<String, StateMachine> activeStateMachines;
  private List<Long> resetAt;
  private List<Long> actionDoneAt;
  private String behaviourTagName;
}
