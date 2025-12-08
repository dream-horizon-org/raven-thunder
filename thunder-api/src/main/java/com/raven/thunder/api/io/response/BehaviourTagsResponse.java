package com.raven.thunder.api.io.response;

import com.raven.thunder.core.model.BehaviourTag;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviourTagsResponse {
  public List<BehaviourTag> behaviourTags;
}
