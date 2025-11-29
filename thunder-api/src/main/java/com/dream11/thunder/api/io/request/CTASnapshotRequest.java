package com.dream11.thunder.api.io.request;

import com.dream11.thunder.api.model.BehaviourTagSnapshot;
import com.dream11.thunder.api.model.StateMachineSnapshot;
import java.util.List;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request containing current state snapshot from client including CTAs, " +
                     "active state machines, and behaviour tags")
@Data
public class CTASnapshotRequest {
  @Schema(description = "List of CTA state machine snapshots containing active state machines, " +
                       "reset times, and action done times")
  private List<StateMachineSnapshot> ctas;
  
  @Schema(description = "List of behaviour tag snapshots containing exposure rules and CTA relations")
  private List<BehaviourTagSnapshot> behaviourTags;
}
