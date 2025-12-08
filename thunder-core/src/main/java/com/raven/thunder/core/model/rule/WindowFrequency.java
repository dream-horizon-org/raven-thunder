package com.raven.thunder.core.model.rule;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WindowFrequency {

  @NotNull private Integer limit;
  @NotNull private WindowFrequencyUnit unit;
  @NotNull private Integer value;
}
