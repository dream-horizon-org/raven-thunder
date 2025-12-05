package com.dream11.thunder.admin.service.admin;

import com.dream11.thunder.admin.exception.DefinedException;
import com.dream11.thunder.admin.exception.ErrorEntity;
import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.core.dao.cta.CTADetails;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import java.util.List;

public class CTAUpdateValidator {

  private final List<CTAStatus> nonEditableStates =
      List.of(CTAStatus.SCHEDULED, CTAStatus.LIVE, CTAStatus.CONCLUDED, CTAStatus.TERMINATED);

  private final DraftCTAUpdateValidator draftCTAUpdateValidator = new DraftCTAUpdateValidator();
  private final PausedCTAUpdateValidator pausedCTAUpdateValidator = new PausedCTAUpdateValidator();

  /**
   * Validates update eligibility based on current CTA status and delegates to the appropriate
   * validator (DRAFT or PAUSED).
   *
   * @throws DefinedException when updates are not allowed for current status
   */
  public CTA apply(CTAUpdateRequest ctaRequest, CTADetails ctaDetails, String user, Long ctaId) {
    if (nonEditableStates.contains(ctaDetails.getCtaStatus())) {
      throw new DefinedException(ErrorEntity.CTA_UPDATE_NOT_ALLOWED);
    }

    if (ctaDetails.getCtaStatus().equals(CTAStatus.DRAFT)) {
      return draftCTAUpdateValidator.apply(ctaRequest, ctaDetails, user, ctaId);
    }

    return pausedCTAUpdateValidator.apply(ctaRequest, ctaDetails, user, ctaId);
  }
}
