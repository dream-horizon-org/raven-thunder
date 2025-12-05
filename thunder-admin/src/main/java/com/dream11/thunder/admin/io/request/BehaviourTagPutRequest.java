package com.dream11.thunder.admin.io.request;

import com.dream11.thunder.core.model.CTARelation;
import com.dream11.thunder.core.model.ExposureRule;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviourTagPutRequest {
  private String description;
  private ExposureRule exposureRule;
  private CTARelation ctaRelation;
  private Set<String> linkedCtas;

  public void validate(String behaviourTagName) {
    BehaviourTagCreateRequest.checkIfBasicDetailsAreNull(
        behaviourTagName, exposureRule, linkedCtas, ctaRelation);
  }
}
