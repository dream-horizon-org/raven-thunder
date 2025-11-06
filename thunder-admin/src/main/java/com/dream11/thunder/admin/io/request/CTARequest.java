package com.dream11.thunder.admin.io.request;

import static com.dream11.thunder.admin.constant.Constants.ALLOWED_DIFF_FROM_CURRENT_TIME_IN_MIN;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.exception.ErrorEntity;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTARequest {
  private String name;
  private List<String> tags;
  @Nullable private String description;
  private String team;
  @Nullable private Long startTime;
  @Nullable private Long endTime;
  private RuleRequest rule;

  public void validate() {
    verifyStartAndEndDate(startTime, endTime);
  }

  private void verifyStartAndEndDate(Long startTime, Long endTime) {
    if (startTime != null
        && Duration.between(Instant.ofEpochMilli(startTime), Instant.now()).toMinutes()
            >= ALLOWED_DIFF_FROM_CURRENT_TIME_IN_MIN) {
      throw new DefinedException(ErrorEntity.INVALID_CTA_TIME);
    }
    if (endTime != null
        && Duration.between(Instant.ofEpochMilli(endTime), Instant.now()).toMinutes()
            >= ALLOWED_DIFF_FROM_CURRENT_TIME_IN_MIN) {
      throw new DefinedException(ErrorEntity.INVALID_CTA_TIME);
    }
    if (startTime != null && endTime != null && endTime < startTime) {
      throw new DefinedException(ErrorEntity.INVALID_CTA_END_TIME);
    }
  }
}

