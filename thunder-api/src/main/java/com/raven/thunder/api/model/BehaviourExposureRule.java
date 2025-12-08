package com.raven.thunder.api.model;

import com.raven.thunder.core.model.rule.LifespanFrequency;
import com.raven.thunder.core.model.rule.SessionFrequency;
import com.raven.thunder.core.model.rule.WindowFrequency;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviourExposureRule {
  @Valid private SessionFrequency session;
  @Valid private LifespanFrequency lifespan;
  @Valid private WindowFrequency window;
  private List<CTAReset> ctasResetAt;
}
