package com.dream11.thunder.core.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupByConfig {
  private Integer maxActiveStateMachineCount;
  private List<String> groupByKeys;
}
