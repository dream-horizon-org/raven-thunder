package com.raven.thunder.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTAReset {
  private String ctaId;
  private Long resetAt;
}
