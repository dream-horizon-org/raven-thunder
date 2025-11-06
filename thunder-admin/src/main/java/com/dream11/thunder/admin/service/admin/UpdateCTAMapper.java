package com.dream11.thunder.admin.service.admin;

import com.dream11.thunder.admin.io.request.CTAUpdateRequest;
import com.dream11.thunder.core.dao.cta.CTADetails;
import com.dream11.thunder.core.model.CTA;

public class UpdateCTAMapper {

  RuleMapper ruleMapper = new RuleMapper();

  public CTA apply(CTAUpdateRequest ctaRequest, CTADetails cta, String user, Long ctaId) {
    return new CTA(
        ctaId,
        ruleMapper.apply(ctaRequest.getRule(), ctaRequest.getEndTime()),
        cta.getCtaStatus(),
        cta.getName(),
        ctaRequest.getDescription(),
        ctaRequest.getTags(),
        ctaRequest.getTeam(),
        cta.getBehaviourTags(),
        ctaRequest.getStartTime(),
        ctaRequest.getEndTime(),
        cta.getCreatedAt(),
        cta.getCreatedBy(),
        System.currentTimeMillis(),
        user,
        cta.getTenantId());
  }
}

