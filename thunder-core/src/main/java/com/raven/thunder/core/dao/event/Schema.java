package com.raven.thunder.core.dao.event;

interface Schema {
  String SET = "event_schema";
  String EVENT_DATA_BIN = "event_data";
  String TENANT_BIN = "tenant_id";
  String EVENT_NAME_BIN = "event_name";
  String TENANT_ID_INDEX = "idx_event_schema_tenantId";
}
