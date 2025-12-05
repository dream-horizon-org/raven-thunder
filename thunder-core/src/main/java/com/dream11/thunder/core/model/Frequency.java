package com.dream11.thunder.core.model;

import com.dream11.thunder.core.model.rule.LifespanFrequency;
import com.dream11.thunder.core.model.rule.SessionFrequency;
import com.dream11.thunder.core.model.rule.WindowFrequency;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Frequency {

  @Valid private SessionFrequency session;
  @Valid private WindowFrequency window;
  @Valid private LifespanFrequency lifeSpan;

  /*
   * supported only once archival process is in place
   * @Valid private LifespanFrequency lifespan;
   */
}
