package com.dream11.thunder.core.dao.behaviourTag;

interface Schema {
  String SET = "behaviour_tag";
  String ID_BIN = "id";
  String NAME_BIN = "name";
  String DESCRIPTION_BIN = "description";
  String EXPOSURE_RULE_BIN = "exposure_rule";
  String CTA_RELATION_BIN = "cta_relation";
  String CREATED_BY_BIN = "created_by";
  String CREATED_AT_BIN = "created_at";
  String LAST_UPDATED_BY_BIN = "last_updated_by";
  String LAST_UPDATED_AT_BIN = "last_updated_at";
  String LINKED_CTAS = "linked_ctas";
  String TENANT_BIN = "tenantId";
  
  String COUNTER_BIN = "counter_time";
  String BEHAVIOUR_TAG_COUNTER = "behaviour_tag_counter";
  String GLOBAL_BEHAVIOUR_TAG_KEY = "global_behaviour_tag_key";
}

