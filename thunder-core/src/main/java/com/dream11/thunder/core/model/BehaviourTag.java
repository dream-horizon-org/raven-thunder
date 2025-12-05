package com.dream11.thunder.core.model;

import java.util.Set;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviourTag {
  private String name;
  private String description;
  private long createdAt;
  private String createdBy;
  private long lastUpdatedAt;
  private String lastUpdatedBy;
  @Nullable private ExposureRule exposureRule;
  @Nullable private CTARelation ctaRelation;
  private Set<String> linkedCtas;
  private String tenantId;
}
