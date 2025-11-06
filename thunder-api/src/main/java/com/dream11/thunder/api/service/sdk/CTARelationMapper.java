package com.dream11.thunder.api.service.sdk;

import com.dream11.thunder.api.model.CTARelationSnapshot;
import com.dream11.thunder.core.model.CTARelation;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;
import java.util.Collections;

public class CTARelationMapper implements Function<CTARelation, CTARelationSnapshot> {
  @Override
  public CTARelationSnapshot apply(@io.reactivex.rxjava3.annotations.NonNull CTARelation ctaRelation) {
    return new CTARelationSnapshot(
        ctaRelation.getShownCta(), ctaRelation.getHideCta(), Collections.emptyList());
  }
}
