package com.raven.thunder.admin.io.request;

import com.raven.thunder.core.model.CTARelation;
import com.raven.thunder.core.model.ExposureRule;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "Request to update an existing Behaviour Tag. "
            + "All fields are optional - only provided fields will be updated.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviourTagPutRequest {
  @Schema(
      description = "Updated description of the Behaviour Tag",
      examples = {"Updated description for onboarding eligible users"})
  private String description;

  @Schema(
      description =
          "Updated exposure rules defining when this behaviour tag applies to users. "
              + "Includes session, window, and lifespan frequency limits.",
      examples = {
        "{\"session\": {\"limit\": 1}, \"window\": {\"limit\": 1, \"unit\": \"days\", \"value\": 7}, \"lifespan\": {\"limit\": 5}}"
      })
  private ExposureRule exposureRule;

  @Schema(
      description =
          "Updated CTA relation rules defining which CTAs are shown/hidden for this behaviour tag. "
              + "shownCta defines which CTAs to show (rule: LIST/ANY/NONE), "
              + "hideCta defines which CTAs to hide (rule: LIST/ANY/NONE).",
      examples = {"{\"shownCta\": {\"rule\": \"ANY\"}, \"hideCta\": {\"rule\": \"NONE\"}}"})
  private CTARelation ctaRelation;

  @Schema(
      description = "Updated set of CTA IDs linked to this behaviour tag",
      examples = {"[]"})
  private Set<String> linkedCtas;

  public void validate(String behaviourTagName) {
    BehaviourTagCreateRequest.checkIfBasicDetailsAreNull(
        behaviourTagName, exposureRule, linkedCtas, ctaRelation);
  }
}
