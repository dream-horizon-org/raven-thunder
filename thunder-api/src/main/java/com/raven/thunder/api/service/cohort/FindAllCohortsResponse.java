package com.raven.thunder.api.service.cohort;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/** Response DTO for the cohort service findAllCohorts endpoint. */
@Data
public class FindAllCohortsResponse {

  @JsonProperty("data")
  private List<String> data;
}
