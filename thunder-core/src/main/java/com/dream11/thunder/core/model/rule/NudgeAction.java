package com.dream11.thunder.core.model.rule;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NudgeAction extends Action {

  @NotNull private String nudgeId;
}

