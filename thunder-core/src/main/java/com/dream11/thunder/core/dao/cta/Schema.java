package com.dream11.thunder.core.dao.cta;

public interface Schema {
  String SET = "cta";
  String RULE_BIN = "rule";
  String STATUS_BIN = "status";
  String NAME_BIN = "name";
  String ID_BIN = "id";
  String TAGS_BIN = "tags";
  String TEAM_BIN = "team";
  String BEHAVIOUR_TAG_BIN = "behaviour_tags";
  String DESCRIPTION_BIN = "description";
  String CREATED_BY_BIN = "created_by";
  String CREATED_AT_BIN = "created_at";
  String LAST_UPDATED_BY_BIN = "last_updated_by";
  String LAST_UPDATED_AT_BIN = "last_updated_at";
  String START_TIME_BIN = "start_time";
  String END_TIME_BIN = "end_time";

  String COUNTER_BIN = "counter_time";
  String TENANT_BIN = "tenantId";

  /** ** Meta info */
  String META_SET = "meta_set";

  String TAGS_META_BIN = "tags";
  String TEAM_META_BIN = "teams";
  String CREATED_BY_META_BIN = "created_By";
  String NAME_META_BIN = "name";
  String CTA_COUNTER = "cta_counter";
  String GLOBAL_CTA_KEY = "global_cta_key";
}

