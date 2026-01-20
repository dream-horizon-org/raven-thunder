package com.raven.thunder.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raven.thunder.core.model.rule.Rule;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCTA {
  @NotNull private Long id;
  private Rule rule;
  private long createdAt;
  private long expiresAt;
  private long expiresInMinutes;
  private String createdBy;
  private String tenantId;
  private List<Long> userIds = Collections.emptyList();
}
