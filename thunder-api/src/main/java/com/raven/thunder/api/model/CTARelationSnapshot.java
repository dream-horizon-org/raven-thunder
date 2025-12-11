package com.raven.thunder.api.model;

import com.raven.thunder.core.model.CtaRelationRule;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CTARelationSnapshot {
  private CtaRelationRule shownCta;
  private CtaRelationRule hideCta;
  private List<String> activeCtas;
}
