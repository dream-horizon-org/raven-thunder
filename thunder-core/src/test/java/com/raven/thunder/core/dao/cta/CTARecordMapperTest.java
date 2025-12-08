package com.raven.thunder.core.dao.cta;

import static org.assertj.core.api.Assertions.assertThat;

import com.aerospike.client.Record;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.CTAStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CTARecordMapperTest {

  @Test
  void mapsRecordToCTA() throws Exception {
    Map<String, Object> bins = new HashMap<>();
    bins.put(Schema.NAME_BIN, "Welcome");
    bins.put(Schema.ID_BIN, 99L);
    bins.put(
        Schema.RULE_BIN,
        "{\"stateToAction\":{\"A\":\"doA\"},\"resetCTAonFirstLaunch\":false,"
            + "\"priority\":1,\"stateTransition\":{},\"actions\":[{}],\"frequency\":{}}");
    bins.put(Schema.BEHAVIOUR_TAG_BIN, List.of("bt1"));
    bins.put(Schema.TAGS_BIN, List.of("tag1"));
    bins.put(Schema.TEAM_BIN, "growth");
    bins.put(Schema.DESCRIPTION_BIN, "desc");
    bins.put(Schema.CREATED_AT_BIN, 1000L);
    bins.put(Schema.CREATED_BY_BIN, "alice");
    bins.put(Schema.LAST_UPDATED_AT_BIN, 2000L);
    bins.put(Schema.LAST_UPDATED_BY_BIN, "bob");
    bins.put(Schema.STATUS_BIN, CTAStatus.DRAFT.name());
    bins.put(Schema.START_TIME_BIN, 10L);
    bins.put(Schema.END_TIME_BIN, 20L);
    bins.put(Schema.TENANT_BIN, "tenant-1");
    Record record = new Record(bins, 0, 0);

    CTARecordMapper mapper = new CTARecordMapper();
    CTA out = mapper.apply(record);

    assertThat(out.getId()).isEqualTo(99L);
    assertThat(out.getName()).isEqualTo("Welcome");
    assertThat(out.getCtaStatus()).isEqualTo(CTAStatus.DRAFT);
    assertThat(out.getTags()).containsExactly("tag1");
    assertThat(out.getBehaviourTags()).containsExactly("bt1");
    assertThat(out.getTeam()).isEqualTo("growth");
    assertThat(out.getDescription()).isEqualTo("desc");
    assertThat(out.getCreatedAt()).isEqualTo(1000L);
    assertThat(out.getCreatedBy()).isEqualTo("alice");
    assertThat(out.getLastUpdatedAt()).isEqualTo(2000L);
    assertThat(out.getLastUpdatedBy()).isEqualTo("bob");
    assertThat(out.getStartTime()).isEqualTo(10L);
    assertThat(out.getEndTime()).isEqualTo(20L);
    assertThat(out.getTenantId()).isEqualTo("tenant-1");
    // rule minimally parsed
    assertThat(out.getRule()).isNotNull();
  }
}
