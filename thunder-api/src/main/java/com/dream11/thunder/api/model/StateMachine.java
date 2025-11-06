package com.dream11.thunder.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StateMachine {

  @NotNull private String currentState;
  @NotNull private Long lastTransitionAt;
  private Map<String, Object> context;
  @NotNull private Long createdAt;
  private Boolean reset;
}
