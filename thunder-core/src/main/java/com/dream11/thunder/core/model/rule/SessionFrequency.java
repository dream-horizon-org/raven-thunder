package com.dream11.thunder.core.model.rule;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionFrequency {

  @NotNull private Integer limit;
}

