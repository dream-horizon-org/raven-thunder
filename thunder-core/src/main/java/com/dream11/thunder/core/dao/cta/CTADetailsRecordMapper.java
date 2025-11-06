package com.dream11.thunder.core.dao.cta;

import com.aerospike.client.Record;
import com.dream11.thunder.core.model.CTAStatus;
import com.dream11.thunder.core.model.rule.Rule;
import com.dream11.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;
import java.util.List;

public class CTADetailsRecordMapper implements Function<Record, CTADetails> {

  @Override
  public CTADetails apply(@NonNull Record record) throws Exception {

    CTADetails ctaDetails = new CTADetails();

    if (record.bins.get(Schema.NAME_BIN) != null) {
      ctaDetails.setName(record.bins.get(Schema.NAME_BIN).toString());
    }

    if (record.bins.get(Schema.ID_BIN) != null) {
      ctaDetails.setId(Long.valueOf(record.bins.get(Schema.ID_BIN).toString()));
    }

    if (record.bins.get(Schema.RULE_BIN) != null) {
      ctaDetails.setRule(ParseUtil.parse(record.bins.get(Schema.RULE_BIN).toString(), Rule.class));
    }

    if (record.bins.get(Schema.BEHAVIOUR_TAG_BIN) != null) {
      ctaDetails.setBehaviourTags((List<String>) record.bins.get(Schema.BEHAVIOUR_TAG_BIN));
    }

    if (record.bins.get(Schema.TAGS_BIN) != null) {
      ctaDetails.setTags((List<String>) record.bins.get(Schema.TAGS_BIN));
    }

    if (record.bins.get(Schema.TEAM_BIN) != null) {
      ctaDetails.setTeam(record.bins.get(Schema.TEAM_BIN).toString());
    }

    if (record.bins.get(Schema.DESCRIPTION_BIN) != null) {
      ctaDetails.setDescription(record.bins.get(Schema.DESCRIPTION_BIN).toString());
    }

    if (record.bins.get(Schema.CREATED_AT_BIN) != null) {
      ctaDetails.setCreatedAt(Long.parseLong(record.bins.get(Schema.CREATED_AT_BIN).toString()));
    }

    if (record.bins.get(Schema.CREATED_BY_BIN) != null) {
      ctaDetails.setCreatedBy(record.bins.get(Schema.CREATED_BY_BIN).toString());
    }

    if (record.bins.get(Schema.LAST_UPDATED_AT_BIN) != null) {
      ctaDetails.setLastUpdatedAt(
          Long.parseLong(record.bins.get(Schema.LAST_UPDATED_AT_BIN).toString()));
    }

    if (record.bins.get(Schema.LAST_UPDATED_BY_BIN) != null) {
      ctaDetails.setLastUpdatedBy(record.bins.get(Schema.LAST_UPDATED_BY_BIN).toString());
    }

    if (record.bins.get(Schema.RULE_BIN) != null) {
      ctaDetails.setRule(ParseUtil.parse((String) record.bins.get(Schema.RULE_BIN), Rule.class));
    }

    if (record.bins.get(Schema.STATUS_BIN) != null) {
      ctaDetails.setCtaStatus(CTAStatus.valueOf((String) record.bins.get(Schema.STATUS_BIN)));
    }

    if (record.bins.get(Schema.START_TIME_BIN) != null) {
      ctaDetails.setStartTime(Long.parseLong(record.bins.get(Schema.START_TIME_BIN).toString()));
    }

    if (record.bins.get(Schema.END_TIME_BIN) != null) {
      ctaDetails.setEndTime(Long.parseLong(record.bins.get(Schema.END_TIME_BIN).toString()));
    }

    if (record.bins.get(Schema.TENANT_BIN) != null) {
      ctaDetails.setTenantId(record.bins.get(Schema.TENANT_BIN).toString());
    }

    ctaDetails.setGenerationId(record.generation);

    return ctaDetails;
  }
}

