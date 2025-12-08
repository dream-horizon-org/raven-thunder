package com.raven.thunder.admin.service.admin;

import com.raven.thunder.admin.io.request.CTAUpdateRequest;
import com.raven.thunder.core.dao.cta.CTADetails;
import com.raven.thunder.core.model.CTA;

public class PausedCTAUpdateValidator {

  private final UpdateCTAMapper updateCTAMapper = new UpdateCTAMapper();

  /**
   * Validates and maps updates for a CTA currently in PAUSED status. Additional PAUSED-specific
   * rules can be enforced here.
   */
  public CTA apply(CTAUpdateRequest ctaRequest, CTADetails ctaDetails, String user, Long ctaId) {
    // TODO: add more conditions
    return updateCTAMapper.apply(ctaRequest, ctaDetails, user, ctaId);
  }
}
