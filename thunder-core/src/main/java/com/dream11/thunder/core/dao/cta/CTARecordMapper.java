package com.dream11.thunder.core.dao.cta;

import com.aerospike.client.Record;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import com.dream11.thunder.core.model.rule.Rule;
import com.dream11.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;
import java.util.List;

public class CTARecordMapper implements Function<Record, CTA> {
  @Override
  public CTA apply(@NonNull Record record) throws Exception {
    CTA cta = new CTA();

    if (record.bins.get(Schema.NAME_BIN) != null) {
      cta.setName(record.bins.get(Schema.NAME_BIN).toString());
    }

    if (record.bins.get(Schema.ID_BIN) != null) {
      cta.setId(Long.valueOf(record.bins.get(Schema.ID_BIN).toString()));
    }

    if (record.bins.get(Schema.RULE_BIN) != null) {
      cta.setRule(ParseUtil.parse((String) record.bins.get(Schema.RULE_BIN), Rule.class));
    }

    if (record.bins.get(Schema.BEHAVIOUR_TAG_BIN) != null) {
      cta.setBehaviourTags((List<String>) record.bins.get(Schema.BEHAVIOUR_TAG_BIN));
    }

    if (record.bins.get(Schema.TAGS_BIN) != null) {
      cta.setTags((List<String>) record.bins.get(Schema.TAGS_BIN));
    }

    if (record.bins.get(Schema.TEAM_BIN) != null) {
      cta.setTeam(record.bins.get(Schema.TEAM_BIN).toString());
    }

    if (record.bins.get(Schema.DESCRIPTION_BIN) != null) {
      cta.setDescription(record.bins.get(Schema.DESCRIPTION_BIN).toString());
    }

    if (record.bins.get(Schema.CREATED_AT_BIN) != null) {
      cta.setCreatedAt((Long) record.bins.get(Schema.CREATED_AT_BIN));
    }

    if (record.bins.get(Schema.CREATED_BY_BIN) != null) {
      cta.setCreatedBy(record.bins.get(Schema.CREATED_BY_BIN).toString());
    }

    if (record.bins.get(Schema.LAST_UPDATED_AT_BIN) != null) {
      cta.setLastUpdatedAt((Long) record.bins.get(Schema.LAST_UPDATED_AT_BIN));
    }

    if (record.bins.get(Schema.LAST_UPDATED_BY_BIN) != null) {
      cta.setLastUpdatedBy(record.bins.get(Schema.LAST_UPDATED_BY_BIN).toString());
    }

    if (record.bins.get(Schema.STATUS_BIN) != null) {
      cta.setCtaStatus(CTAStatus.valueOf((String) record.bins.get(Schema.STATUS_BIN)));
    }

    if (record.bins.get(Schema.START_TIME_BIN) != null) {
      cta.setStartTime((Long) record.bins.get(Schema.START_TIME_BIN));
    }

    if (record.bins.get(Schema.END_TIME_BIN) != null) {
      cta.setEndTime((Long) record.bins.get(Schema.END_TIME_BIN));
    }

    if (record.bins.get(Schema.TENANT_BIN) != null) {
      cta.setTenantId(record.bins.get(Schema.TENANT_BIN).toString());
    }

    return cta;
  }
}

