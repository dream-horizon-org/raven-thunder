package com.dream11.thunder.api.service.sdk;

import com.dream11.thunder.api.model.BehaviourTagSnapshot;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps a list of BehaviourTagSnapshot into a Map keyed by behaviourTagName.
 */
public class BehaviourTagMapper
    implements Function<List<BehaviourTagSnapshot>, Map<String, BehaviourTagSnapshot>> {
  @Override
  public Map<String, BehaviourTagSnapshot> apply(
      @io.reactivex.rxjava3.annotations.NonNull List<BehaviourTagSnapshot> behaviourTagSnapshots) {
    try {
      return behaviourTagSnapshots.stream()
          .collect(Collectors.toMap(BehaviourTagSnapshot::getBehaviourTagName, entity -> entity));
    } catch (Exception e) {
      return Collections.emptyMap();
    }
  }
}
