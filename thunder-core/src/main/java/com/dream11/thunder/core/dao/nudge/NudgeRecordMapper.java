package com.dream11.thunder.core.dao.nudge;

import com.aerospike.client.Record;
import com.dream11.thunder.core.model.Nudge;
import com.dream11.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.functions.Function;

class NudgeRecordMapper implements Function<Record, Nudge> {
  @Override
  public Nudge apply(Record record) throws Exception {
    if (record.bins.get(Schema.TEMPLATE_BIN) == null) {
      return null;
    }

    return ParseUtil.parse((String) record.bins.get(Schema.TEMPLATE_BIN), Nudge.class);
  }
}

