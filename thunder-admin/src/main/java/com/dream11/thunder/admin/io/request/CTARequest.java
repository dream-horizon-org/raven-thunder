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
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

@Schema(
    description = "Request to create a new CTA (Call-to-Action). " +
                  "Note: Cohort eligibility must use 'all' as the cohort value. " +
                  "Use includes: [\"all\"] for single cohort or includes: [\"all\"] for list, " +
                  "and always keep excludes: [] empty. User-cohorts system is not currently supported."
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTARequest {
  @Schema(description = "Name of the CTA", examples = {"Testing1", "Welcome Bonus"}, required = true, maxLength = 100)
  private String name;
  
  @Schema(description = "Tags associated with the CTA for categorization", 
         examples = {"growth", "notifications"})
  private List<String> tags;
  
  @Schema(description = "Detailed description of the CTA", 
         examples = {"Bottom sheet to prompt enabling notifications when lineups are out"},
         maxLength = 500)
  @Nullable private String description;
  
  @Schema(description = "Team that owns this CTA", examples = {"marketing"}, required = true)
  private String team;
  
  @Schema(description = "Start time in epoch milliseconds. If null, CTA starts immediately when activated.",
         examples = {"1609459200000"}, type = SchemaType.INTEGER, format = "int64")
  @Nullable private Long startTime;
  
  @Schema(description = "End time in epoch milliseconds. If null, CTA has no end time.",
         examples = {"1640995200000"}, type = SchemaType.INTEGER, format = "int64")
  @Nullable private Long endTime;
  
  @Schema(
      description = "Rule configuration for the CTA including cohort eligibility, state transitions, " +
                   "actions, and frequency controls. " +
                   "IMPORTANT: cohortEligibility.includes must be [\"all\"] and excludes must be []. " +
                   "User-cohorts system is not currently supported.",
      required = true
  )
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

