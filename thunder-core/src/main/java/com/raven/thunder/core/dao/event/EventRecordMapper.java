package com.raven.thunder.core.dao.event;

import com.aerospike.client.Record;
import com.raven.thunder.core.model.EventData;
import com.raven.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;

public class EventRecordMapper implements Function<Record, EventData> {
  @Override
  public EventData apply(@NonNull Record record) throws Exception {
    if (record.bins.get(Schema.EVENT_DATA_BIN) == null) {
      return null;
    }
    return ParseUtil.parse(record.bins.get(Schema.EVENT_DATA_BIN).toString(), EventData.class);
  }
}
