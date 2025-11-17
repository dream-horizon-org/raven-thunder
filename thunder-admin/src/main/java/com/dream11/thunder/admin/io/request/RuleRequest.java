package com.dream11.thunder.admin.io.request;

import com.dream11.thunder.core.model.CohortEligibility;
import com.dream11.thunder.core.model.Frequency;
import com.dream11.thunder.core.model.GroupByConfig;
import com.dream11.thunder.core.model.rule.StateTransitionCondition;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleRequest {
  @NotNull @Valid private CohortEligibility cohortEligibility;
  @NotEmpty Map<String, String> stateToAction;
  private List<String> resetStates;
  private Boolean resetCTAonFirstLaunch;
  private List<String> contextParams;
  @NotNull @Valid private Map<String, Map<String, List<StateTransitionCondition>>> stateTransition;
  private GroupByConfig groupByConfig;
  @NotNull private Integer priority;
  @NotNull private Long stateMachineTTL; /* milliseconds */
  @NotNull @Valid private List<Map<String, Object>> actions;
  @NotNull @Valid private Frequency frequency;
}

