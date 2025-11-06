package com.dream11.thunder.core.model.rule;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LifespanFrequency {

  @NotNull private Integer limit;
}

