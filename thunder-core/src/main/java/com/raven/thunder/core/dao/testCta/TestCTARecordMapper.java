package com.raven.thunder.core.dao.testCta;

import com.aerospike.client.Record;
import com.raven.thunder.core.model.TestCTA;
import com.raven.thunder.core.model.rule.Rule;
import com.raven.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;
import java.util.ArrayList;
import java.util.List;

public class TestCTARecordMapper implements Function<Record, TestCTA> {
  @Override
  public TestCTA apply(@NonNull Record record) throws Exception {
    TestCTA testCTA = new TestCTA();

    if (record.bins.get(Schema.ID_BIN) != null) {
      testCTA.setId(Long.valueOf(record.bins.get(Schema.ID_BIN).toString()));
    }

    if (record.bins.get(Schema.RULE_BIN) != null) {
      testCTA.setRule(ParseUtil.parse((String) record.bins.get(Schema.RULE_BIN), Rule.class));
    }

    if (record.bins.get(Schema.CREATED_AT_BIN) != null) {
      testCTA.setCreatedAt((Long) record.bins.get(Schema.CREATED_AT_BIN));
    }

    if (record.bins.get(Schema.EXPIRES_AT_BIN) != null) {
      testCTA.setExpiresAt((Long) record.bins.get(Schema.EXPIRES_AT_BIN));
    }

    if (record.bins.get(Schema.CREATED_BY_BIN) != null) {
      testCTA.setCreatedBy(record.bins.get(Schema.CREATED_BY_BIN).toString());
    }

    if (record.bins.get(Schema.TENANT_BIN) != null) {
      testCTA.setTenantId(record.bins.get(Schema.TENANT_BIN).toString());
    }

    if (record.bins.get(Schema.USER_IDS_BIN) != null) {
      Object raw = record.bins.get(Schema.USER_IDS_BIN);
      if (raw instanceof List) {
        List<?> rawList = (List<?>) raw;
        List<Long> userIds = new ArrayList<>(rawList.size());
        for (Object o : rawList) {
          if (o instanceof Number) {
            userIds.add(((Number) o).longValue());
          } else if (o != null) {
            try {
              userIds.add(Long.parseLong(o.toString()));
            } catch (NumberFormatException ignore) {
              // skip invalid entries
            }
          }
        }
        testCTA.setUserIds(userIds);
      }
    }

    return testCTA;
  }
}
