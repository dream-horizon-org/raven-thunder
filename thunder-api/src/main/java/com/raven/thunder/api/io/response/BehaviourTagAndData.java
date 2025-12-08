package com.raven.thunder.api.io.response;

import com.raven.thunder.api.model.BehaviourExposureRule;
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
