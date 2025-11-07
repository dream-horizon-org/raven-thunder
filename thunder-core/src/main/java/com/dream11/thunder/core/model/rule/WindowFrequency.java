package com.dream11.thunder.core.model.rule;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WindowFrequency {

  @NotNull private Integer limit;
  @NotNull private WindowFrequencyUnit unit;
  @NotNull private Integer value;
}

