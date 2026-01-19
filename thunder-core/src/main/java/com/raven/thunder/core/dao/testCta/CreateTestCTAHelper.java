package com.raven.thunder.core.dao.testCta;

import com.aerospike.client.Bin;
import com.aerospike.client.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.raven.thunder.core.model.TestCTA;
import com.raven.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;

public class CreateTestCTAHelper {

  public Bin[] apply(String tenantId, @NonNull TestCTA cta) throws JsonProcessingException {
    List<Bin> binList = new ArrayList<>();
    binList.add(
        new Bin(
            Schema.RULE_BIN, new Value.StringValue(ParseUtil.writeValueAsString(cta.getRule()))));
    binList.add(new Bin(Schema.ID_BIN, new Value.LongValue(cta.getId())));
    binList.add(new Bin(Schema.CREATED_AT_BIN, new Value.LongValue(cta.getCreatedAt())));
    binList.add(new Bin(Schema.EXPIRES_AT_BIN, new Value.LongValue(cta.getExpiresAt())));
    binList.add(new Bin(Schema.CREATED_BY_BIN, new Value.StringValue(cta.getCreatedBy())));
    binList.add(new Bin(Schema.TENANT_BIN, new Value.StringValue(tenantId)));
    binList.add(new Bin(Schema.USER_IDS_BIN, cta.getUserIds()));
    return binList.toArray(new Bin[0]);
  }
}
