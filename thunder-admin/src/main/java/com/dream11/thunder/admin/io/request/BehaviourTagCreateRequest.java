package com.dream11.thunder.admin.io.request;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.exception.ErrorEntity;
import com.dream11.thunder.core.model.CTARelation;
import com.dream11.thunder.core.model.ExposureRule;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviourTagCreateRequest {
  private String behaviourTagName;
  private String description;
  private ExposureRule exposureRule;
  private CTARelation ctaRelation;
  private Set<String> linkedCtas;

  public void validate() {
    checkIfBasicDetailsAreNull(behaviourTagName, exposureRule, linkedCtas, ctaRelation);
  }

  static void checkIfBasicDetailsAreNull(
      String name, ExposureRule exposureRule, Set<String> linkedCtas, CTARelation ctaRelation) {
    if (StringUtils.isBlank(name)) throw new DefinedException(ErrorEntity.INVALID_REQUEST_BODY);

    if (Objects.nonNull(exposureRule)
        && (Objects.isNull(exposureRule.getSession())
            || Objects.isNull(exposureRule.getWindow())
            || Objects.isNull(exposureRule.getLifespan()))) {
      throw new DefinedException(ErrorEntity.INVALID_REQUEST_BODY);
    }

    if (Objects.nonNull(ctaRelation)
        && (Objects.isNull(ctaRelation.getShownCta())
            || Objects.isNull(ctaRelation.getHideCta()))) {
      throw new DefinedException(ErrorEntity.INVALID_REQUEST_BODY);
    }
  }
}
