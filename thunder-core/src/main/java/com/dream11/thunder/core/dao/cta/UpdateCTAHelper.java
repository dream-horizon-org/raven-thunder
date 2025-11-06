package com.dream11.thunder.core.dao.cta;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.util.ParseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;
import java.util.ArrayList;
import java.util.List;

public class UpdateCTAHelper implements Function<CTA, Bin[]> {

  @Override
  public Bin[] apply(@NonNull CTA cta) throws JsonProcessingException {
    List<Bin> binList = new ArrayList<>();
    binList.add(
        new Bin(
            Schema.RULE_BIN, new Value.StringValue(ParseUtil.writeValueAsString(cta.getRule()))));
    binList.add(new Bin(Schema.ID_BIN, new Value.LongValue(cta.getId())));
    binList.add(new Bin(Schema.STATUS_BIN, new Value.StringValue(cta.getCtaStatus().name())));
    binList.add(new Bin(Schema.NAME_BIN, new Value.StringValue(cta.getName())));
    binList.add(new Bin(Schema.DESCRIPTION_BIN, new Value.StringValue(cta.getDescription())));
    binList.add(new Bin(Schema.TAGS_BIN, new Value.ListValue(cta.getTags())));
    binList.add(new Bin(Schema.TEAM_BIN, new Value.StringValue(cta.getTeam())));
    if (cta.getStartTime() != null) {
      binList.add(new Bin(Schema.START_TIME_BIN, new Value.LongValue(cta.getStartTime())));
    }
    if (cta.getEndTime() != null) {
      binList.add(new Bin(Schema.END_TIME_BIN, new Value.LongValue(cta.getEndTime())));
    }
    binList.add(new Bin(Schema.LAST_UPDATED_AT_BIN, new Value.LongValue(cta.getLastUpdatedAt())));
    binList.add(new Bin(Schema.LAST_UPDATED_BY_BIN, new Value.StringValue(cta.getLastUpdatedBy())));
    return binList.toArray(new Bin[0]);
  }
}

