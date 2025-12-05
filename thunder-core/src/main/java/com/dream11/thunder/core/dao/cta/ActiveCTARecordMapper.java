package com.dream11.thunder.core.dao.cta;

import com.aerospike.client.query.KeyRecord;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;

public class ActiveCTARecordMapper implements Function<KeyRecord, ActiveCTA> {

  @Override
  public ActiveCTA apply(@NonNull KeyRecord keyRecord) throws Exception {
    ActiveCTA activeCTA = new ActiveCTA();
    activeCTA.setEndTime((Long) keyRecord.record.bins.get(Schema.END_TIME_BIN));
    activeCTA.setGenerationId(keyRecord.record.generation);
    return activeCTA;
  }
}
