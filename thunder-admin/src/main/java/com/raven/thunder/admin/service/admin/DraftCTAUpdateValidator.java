package com.raven.thunder.admin.service.admin;

import com.raven.thunder.admin.io.request.CTAUpdateRequest;
import com.raven.thunder.core.dao.cta.CTADetails;
import com.raven.thunder.core.model.CTA;

public class DraftCTAUpdateValidator {

  private final UpdateCTAMapper updateCTAMapper = new UpdateCTAMapper();

  /**
   * Validates and maps updates for a CTA currently in DRAFT status. Additional DRAFT-specific rules
   * can be enforced here.
   */
  public CTA apply(CTAUpdateRequest ctaRequest, CTADetails ctaDetails, String user, Long ctaId) {
    // TODO: add more conditions
    return updateCTAMapper.apply(ctaRequest, ctaDetails, user, ctaId);
  }
}
