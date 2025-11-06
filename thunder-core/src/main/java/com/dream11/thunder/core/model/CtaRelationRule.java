package com.dream11.thunder.core.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CtaRelationRule {
  private CtaRelationRuleTypes rule; // FIXME why do we need to store this rule here?
  private Set<String> ctaList;
}

