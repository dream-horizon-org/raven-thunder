package com.raven.thunder.admin.io.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCTARequest {
  private RuleRequest rule;
  @Nullable private Long previousCtaId;
  private Integer expiresInMinutes = 30; // Default to 30 minutes
  private List<Long> userIds = new ArrayList<>();
}
