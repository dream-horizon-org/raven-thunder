package com.raven.thunder.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raven.thunder.core.model.rule.LifespanFrequency;
import com.raven.thunder.core.model.rule.SessionFrequency;
import com.raven.thunder.core.model.rule.WindowFrequency;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Frequency {

  @Valid private SessionFrequency session;
  @Valid private WindowFrequency window;
  @Valid private LifespanFrequency lifespan;

  /*
   * supported only once archival process is in place
   * @Valid private LifespanFrequency lifespan;
   */
}
