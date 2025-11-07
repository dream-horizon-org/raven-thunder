package com.dream11.thunder.admin.io.response;

import com.dream11.thunder.core.model.BehaviourTag;
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

