package com.dream11.thunder.admin.service.admin;

import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.core.dao.cta.CTADetails;
import com.dream11.thunder.core.model.CTA;

public class DraftCTAUpdateValidator {

  private final UpdateCTAMapper updateCTAMapper = new UpdateCTAMapper();

  /**
   * Validates and maps updates for a CTA currently in DRAFT status.
   * Additional DRAFT-specific rules can be enforced here.
   */
  public CTA apply(CTAUpdateRequest ctaRequest, CTADetails ctaDetails, String user, Long ctaId) {
    // TODO: add more conditions
    return updateCTAMapper.apply(ctaRequest, ctaDetails, user, ctaId);
  }
}

