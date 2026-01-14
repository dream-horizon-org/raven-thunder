package com.raven.thunder.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventProperty {
  private String propertyName;
  private String type;
  private String expectedValue;
  private Boolean isMandatory;
  private String description;
}
