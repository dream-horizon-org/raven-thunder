package com.dream11.thunder.core.model;

import com.dream11.thunder.core.model.rule.Rule;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTA {
  @NotNull private Long id;
  private Rule rule;
  private CTAStatus ctaStatus;
  private String name;
  private String description;
  private List<String> tags;
  private String team;
  private List<String> behaviourTags = Collections.emptyList();
  @Nullable private Long startTime;
  @Nullable private Long endTime;
  private long createdAt;
  private String createdBy;
  private Long lastUpdatedAt;
  private String lastUpdatedBy;
  private String tenantId;
}

