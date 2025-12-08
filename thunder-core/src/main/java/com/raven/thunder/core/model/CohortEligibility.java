package com.raven.thunder.core.model;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "Cohort eligibility configuration for CTAs. "
            + "IMPORTANT: User-cohorts system is not currently supported. "
            + "Always use includes: [\"all\"] (single cohort) or includes: [\"all\"] (list), "
            + "and keep excludes: [] empty.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CohortEligibility {

  @Schema(
      description =
          "List of cohort names to include. "
              + "Must be [\"all\"] since user-cohorts system is not currently supported.",
      required = true)
  @NotEmpty
  private List<String> includes;

  @Schema(
      description =
          "List of cohort names to exclude. "
              + "Must always be empty [] since user-cohorts system is not currently supported.",
      required = true)
  @NotNull
  private List<String> excludes;
}
