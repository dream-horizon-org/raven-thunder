package com.dream11.thunder.admin.service.admin;

import com.dream11.thunder.admin.io.request.CTARequest;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import java.util.Collections;

public class CreateCTAMapper {

  RuleMapper ruleMapper = new RuleMapper();

  /**
   * Maps a CTARequest to a new CTA entity in DRAFT status.
   *
   * @param tenantId tenant identifier
   * @param ctaRequest incoming create request
   * @param user user creating the CTA
   * @param ctaId generated CTA id
   * @return CTA model ready for persistence
   */
  public CTA apply(String tenantId, CTARequest ctaRequest, String user, Long ctaId) {
    return new CTA(
        ctaId,
        ruleMapper.apply(ctaRequest.getRule(), ctaRequest.getEndTime()),
        CTAStatus.DRAFT,
        ctaRequest.getName(),
        ctaRequest.getDescription(),
        ctaRequest.getTags(),
        ctaRequest.getTeam(),
        Collections.emptyList(),
        ctaRequest.getStartTime(),
        ctaRequest.getEndTime(),
        System.currentTimeMillis(),
        user,
        0L,
        null,
        tenantId);
  }
}
