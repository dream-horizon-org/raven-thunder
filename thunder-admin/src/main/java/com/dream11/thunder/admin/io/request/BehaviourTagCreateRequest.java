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
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to create a new Behaviour Tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviourTagCreateRequest {
  @Schema(description = "Name of the Behaviour Tag", examples = {"new_user_tag"}, required = true)
  private String behaviourTagName;
  
  @Schema(description = "Description of the Behaviour Tag", 
         examples = {"Users eligible for onboarding nudges"})
  private String description;
  
  @Schema(description = "Exposure rules defining when this behaviour tag applies to users", required = true)
  private ExposureRule exposureRule;
  
  @Schema(description = "CTA relation rules defining which CTAs are shown/hidden for this behaviour tag", required = true)
  private CTARelation ctaRelation;
  
  @Schema(description = "Set of CTA IDs linked to this behaviour tag", examples = {"[]"})
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

