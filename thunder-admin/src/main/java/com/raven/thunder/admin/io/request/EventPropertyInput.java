package com.raven.thunder.admin.io.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPropertyInput {
  @NotNull private String propertyName;
  @NotNull private String type;
  private String expectedValue;
  @NotNull private Boolean isMandatory;
  private String description;
}
