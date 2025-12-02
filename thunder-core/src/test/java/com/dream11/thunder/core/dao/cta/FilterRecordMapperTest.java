package com.dream11.thunder.core.dao.cta;

import com.aerospike.client.Record;
import com.dream11.thunder.core.io.response.FilterResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class FilterRecordMapperTest {

  @Test
  void mapsBinsToFilterResponse() throws Exception {
    Map<String, Object> bins = new HashMap<>();
    bins.put(Schema.TAGS_META_BIN, List.of("a", "b"));
    bins.put(Schema.TEAM_META_BIN, List.of("team1"));
    bins.put(Schema.CREATED_BY_META_BIN, List.of("alice"));
    bins.put(Schema.NAME_META_BIN, List.of("CTA-1"));
    Record record = new Record(bins, 0, 0);

    FilterRecordMapper mapper = new FilterRecordMapper();
    FilterResponse out = mapper.apply(record);

    assertThat(out.getTags()).containsExactly("a", "b");
    assertThat(out.getTeams()).containsExactly("team1");
    assertThat(out.getCreatedBy()).containsExactly("alice");
    assertThat(out.getNames()).containsExactly("CTA-1");
  }

  @Test
  void missingBinsLeaveNulls() throws Exception {
    Record record = new Record(Map.of(), 0, 0);
    FilterRecordMapper mapper = new FilterRecordMapper();
    FilterResponse out = mapper.apply(record);
    assertThat(out.getTags()).isNull();
    assertThat(out.getTeams()).isNull();
    assertThat(out.getCreatedBy()).isNull();
    assertThat(out.getNames()).isNull();
  }
}


