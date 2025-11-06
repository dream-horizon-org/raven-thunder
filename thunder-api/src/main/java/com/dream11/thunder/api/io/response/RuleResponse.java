package com.dream11.thunder.api.io.response;

import com.dream11.thunder.core.model.Frequency;
import com.dream11.thunder.core.model.GroupByConfig;
import com.dream11.thunder.core.model.rule.StateTransitionCondition;
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
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse {
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
