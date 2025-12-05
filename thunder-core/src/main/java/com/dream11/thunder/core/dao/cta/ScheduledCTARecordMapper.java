package com.dream11.thunder.core.dao.cta;

import com.aerospike.client.query.KeyRecord;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;

public class ScheduledCTARecordMapper implements Function<KeyRecord, ScheduledCTA> {

  @Override
  public ScheduledCTA apply(@NonNull KeyRecord record) throws Exception {
    ScheduledCTA scheduledCTA = new ScheduledCTA();
    scheduledCTA.setStartTime((Long) record.record.bins.get(Schema.START_TIME_BIN));
    scheduledCTA.setGenerationId(record.record.generation);
    return scheduledCTA;
  }
}
