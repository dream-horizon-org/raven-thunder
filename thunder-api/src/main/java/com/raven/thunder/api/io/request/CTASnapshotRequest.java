package com.raven.thunder.api.io.request;

import com.raven.thunder.api.model.BehaviourTagSnapshot;
import com.raven.thunder.api.model.StateMachineSnapshot;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "Request containing current state snapshot from client including CTAs, "
            + "active state machines, and behaviour tags. "
            + "This is sent by the client to synchronize state with the server.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTASnapshotRequest {
  @Schema(
      description =
          "List of CTA state machine snapshots. Each snapshot contains: "
              + "ctaId, activeStateMachines (map of state machine IDs to state data), "
              + "resetAt (list of reset timestamps), and actionDoneAt (list of action completion timestamps).",
      examples = {
        "[{\"ctaId\": \"101\", \"activeStateMachines\": {\"5\": {\"currentState\": \"2\", \"lastTransitionAt\": 1720166608502, \"context\": {}, \"createdAt\": 1720166608502, \"reset\": true}}, \"resetAt\": [1701603029000], \"actionDoneAt\": [1756099199923]}]"
      })
  private List<StateMachineSnapshot> ctas;

  @Schema(
      description =
          "List of behaviour tag snapshots. Each snapshot contains: "
              + "behaviourTagName, exposureRule (session/window/lifespan limits), "
              + "and ctaRelation (shownCta/hideCta rules with activeCtas list).",
      examples = {
        "[{\"behaviourTagName\": \"onboarding_eligible\", \"exposureRule\": {\"session\": {\"limit\": 1}, \"window\": {\"limit\": 1, \"unit\": \"days\", \"value\": 7}}, \"ctaRelation\": {\"activeCtas\": [1]}}]"
      })
  private List<BehaviourTagSnapshot> behaviourTags;
}
