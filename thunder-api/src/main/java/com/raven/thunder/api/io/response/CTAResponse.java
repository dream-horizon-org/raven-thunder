package com.raven.thunder.api.io.response;

import com.raven.thunder.api.model.BehaviourTagSnapshot;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTAResponse {
  public List<UserCTAAndStateMachineResponse> ctas;
  public List<BehaviourTagSnapshot> behaviourTags;
}
