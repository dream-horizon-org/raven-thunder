package com.raven.thunder.core.model.rule;

import com.raven.thunder.core.model.CohortEligibility;
import com.raven.thunder.core.model.Frequency;
import com.raven.thunder.core.model.GroupByConfig;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {
  @NotNull @Valid private CohortEligibility cohortEligibility;
  @NotEmpty Map<String, String> stateToAction;
  private List<String> resetStates;
  private Boolean resetCTAonFirstLaunch;
  private List<String> contextParams;
  @NotNull @Valid private Map<String, Map<String, List<StateTransitionCondition>>> stateTransition;
  private GroupByConfig groupByConfig;
  @NotNull private Integer priority;
  @Nullable private Long stateMachineTTL; /* milliseconds */
  @Nullable private Long ctaValidTill; /* milliseconds */
  @NotNull @Valid private List<Map<String, Object>> actions;
  @NotNull @Valid private Frequency frequency;
}
