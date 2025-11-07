package com.dream11.thunder.core.model;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CohortEligibility {

  @NotEmpty private List<String> includes;
  @NotNull private List<String> excludes;
}

