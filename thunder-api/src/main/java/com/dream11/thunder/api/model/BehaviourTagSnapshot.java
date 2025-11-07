package com.dream11.thunder.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BehaviourTagSnapshot {
  private String behaviourTagName;
  private BehaviourExposureRule exposureRule;
  private CTARelationSnapshot ctaRelation;
}
