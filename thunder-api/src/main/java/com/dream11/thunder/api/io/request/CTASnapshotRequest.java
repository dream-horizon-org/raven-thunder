package com.dream11.thunder.api.io.request;

import com.dream11.thunder.api.model.BehaviourTagSnapshot;
import com.dream11.thunder.api.model.StateMachineSnapshot;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTASnapshotRequest {
  private List<StateMachineSnapshot> ctas;
  private List<BehaviourTagSnapshot> behaviourTags;
}
