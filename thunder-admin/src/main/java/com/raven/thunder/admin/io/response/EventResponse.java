package com.raven.thunder.admin.io.response;

import com.raven.thunder.core.model.EventProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
  private String eventName;
  private List<EventProperty> properties;
}
