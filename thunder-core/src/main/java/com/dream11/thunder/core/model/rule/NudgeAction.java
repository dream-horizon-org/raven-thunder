package com.dream11.thunder.core.model.rule;

import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NudgeAction extends Action {

  @NotNull private String nudgeId;
}
