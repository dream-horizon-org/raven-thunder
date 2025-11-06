package com.dream11.thunder.api.io.response;

import com.dream11.thunder.api.model.BehaviourExposureRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviourTagAndData {
  public String behaviorTagName;
  public BehaviourExposureRule exposureRule;
}
