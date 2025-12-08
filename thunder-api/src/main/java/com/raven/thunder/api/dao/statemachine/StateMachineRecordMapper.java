package com.raven.thunder.api.dao.statemachine;

import com.aerospike.client.Record;
import com.raven.thunder.api.model.UserDataSnapshot;
import com.raven.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.functions.Function;

class StateMachineRecordMapper implements Function<Record, UserDataSnapshot> {
  @Override
  public UserDataSnapshot apply(Record record) throws Exception {
    if (record.bins.get(Schema.SNAPSHOT_BIN) == null) {
      return null;
    }

    return ParseUtil.parse((String) record.bins.get(Schema.SNAPSHOT_BIN), UserDataSnapshot.class);
  }
}
