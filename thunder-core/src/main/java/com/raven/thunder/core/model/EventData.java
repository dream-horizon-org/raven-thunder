package com.raven.thunder.core.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventData {
  private String tenantId;
  private String eventName;
  private String source;
  private Long lastSyncedAt;
  private List<EventProperty> properties;
}
