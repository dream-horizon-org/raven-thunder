package com.dream11.thunder.core.dao.cta;

import com.aerospike.client.Record;
import com.dream11.thunder.core.io.response.FilterResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;
import java.util.List;

public class FilterRecordMapper implements Function<Record, FilterResponse> {
  @Override
  public FilterResponse apply(@NonNull Record record) throws Exception {
    FilterResponse filterResponse = new FilterResponse();

    if (record.bins.get(Schema.TAGS_META_BIN) != null) {
      filterResponse.setTags((List<String>) record.bins.get(Schema.TAGS_META_BIN));
    }

    if (record.bins.get(Schema.TEAM_META_BIN) != null) {
      filterResponse.setTeams((List<String>) record.bins.get(Schema.TEAM_META_BIN));
    }

    if (record.bins.get(Schema.CREATED_BY_META_BIN) != null) {
      filterResponse.setCreatedBy((List<String>) record.bins.get(Schema.CREATED_BY_META_BIN));
    }

    if (record.bins.get(Schema.NAME_META_BIN) != null) {
      filterResponse.setNames((List<String>) record.bins.get(Schema.NAME_META_BIN));
    }

    return filterResponse;
  }
}
