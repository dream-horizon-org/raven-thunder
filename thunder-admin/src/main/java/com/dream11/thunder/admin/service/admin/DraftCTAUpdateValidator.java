package com.dream11.thunder.admin.service.admin;

import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.core.dao.cta.CTADetails;
import com.dream11.thunder.core.model.CTA;

public class DraftCTAUpdateValidator {

  private final UpdateCTAMapper updateCTAMapper = new UpdateCTAMapper();

  public CTA apply(CTAUpdateRequest ctaRequest, CTADetails ctaDetails, String user, Long ctaId) {
    // TODO: add more conditions
    return updateCTAMapper.apply(ctaRequest, ctaDetails, user, ctaId);
  }
}

