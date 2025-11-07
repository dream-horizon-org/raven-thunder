package com.dream11.thunder.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTARelation {
  private CtaRelationRule shownCta;
  private CtaRelationRule hideCta;
}

