package com.dream11.thunder.admin.service.admin;

import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.core.dao.cta.CTADetails;
import com.dream11.thunder.core.model.CTA;

public class PausedCTAUpdateValidator {

  private final UpdateCTAMapper updateCTAMapper = new UpdateCTAMapper();

  /**
   * Validates and maps updates for a CTA currently in PAUSED status.
   * Additional PAUSED-specific rules can be enforced here.
   */
  public CTA apply(CTAUpdateRequest ctaRequest, CTADetails ctaDetails, String user, Long ctaId) {
    // TODO: add more conditions
    return updateCTAMapper.apply(ctaRequest, ctaDetails, user, ctaId);
  }
}

