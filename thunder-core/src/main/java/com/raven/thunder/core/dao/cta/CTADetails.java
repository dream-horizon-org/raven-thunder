package com.raven.thunder.core.dao.cta;

import com.raven.thunder.core.model.CTAStatus;
import com.raven.thunder.core.model.rule.Rule;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CTADetails {
  @NotNull private Long id;
  private Rule rule;
  private CTAStatus ctaStatus;
  private String name;
  private String description;
  private List<String> tags;
  private String team;
  private List<String> behaviourTags = Collections.emptyList();
  private Long startTime;
  private Long endTime;
  private Long createdAt;
  private String createdBy;
  private Long lastUpdatedAt;
  private String lastUpdatedBy;
  private int generationId;
  private String tenantId;
}
