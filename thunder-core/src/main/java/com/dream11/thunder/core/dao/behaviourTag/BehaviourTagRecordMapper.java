package com.dream11.thunder.core.dao.behaviourTag;

import com.aerospike.client.Record;
import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.model.CTARelation;
import com.dream11.thunder.core.model.ExposureRule;
import com.dream11.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;
import java.util.HashSet;
import java.util.List;

public class BehaviourTagRecordMapper implements Function<Record, BehaviourTag> {
  @Override
  public BehaviourTag apply(@NonNull Record record) throws Exception {
    BehaviourTag behaviourTag = new BehaviourTag();
    if (record.bins.get(Schema.NAME_BIN) != null) {
      behaviourTag.setName(record.bins.get(Schema.NAME_BIN).toString());
    }

    if (record.bins.get(Schema.DESCRIPTION_BIN) != null) {
      behaviourTag.setDescription(record.bins.get(Schema.DESCRIPTION_BIN).toString());
    }

    if (record.bins.get(Schema.LINKED_CTAS) != null) {
      behaviourTag.setLinkedCtas(new HashSet<>((List<String>) record.bins.get(Schema.LINKED_CTAS)));
    }

    if (record.bins.get(Schema.EXPOSURE_RULE_BIN) != null) {
      behaviourTag.setExposureRule(
          ParseUtil.parse(
              record.bins.get(Schema.EXPOSURE_RULE_BIN).toString(), ExposureRule.class));
    }

    if (record.bins.get(Schema.CTA_RELATION_BIN) != null) {
      behaviourTag.setCtaRelation(
          ParseUtil.parse(record.bins.get(Schema.CTA_RELATION_BIN).toString(), CTARelation.class));
    }

    if (record.bins.get(Schema.CREATED_BY_BIN) != null) {
      behaviourTag.setCreatedBy(record.bins.get(Schema.CREATED_BY_BIN).toString());
    }

    if (record.bins.get(Schema.CREATED_AT_BIN) != null) {
      behaviourTag.setCreatedAt((Long) record.bins.get(Schema.CREATED_AT_BIN));
    }

    if (record.bins.get(Schema.LAST_UPDATED_BY_BIN) != null) {
      behaviourTag.setLastUpdatedBy(record.bins.get(Schema.LAST_UPDATED_BY_BIN).toString());
    }

    if (record.bins.get(Schema.LAST_UPDATED_AT_BIN) != null) {
      behaviourTag.setLastUpdatedAt((Long) record.bins.get(Schema.LAST_UPDATED_AT_BIN));
    }

    if (record.bins.get(Schema.TENANT_BIN) != null) {
      behaviourTag.setTenantId((String) record.bins.get(Schema.TENANT_BIN));
    }

    return behaviourTag;
  }
}

