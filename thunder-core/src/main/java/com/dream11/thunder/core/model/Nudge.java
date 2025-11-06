package com.dream11.thunder.core.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nudge {

  @NotNull private String id;

  @JsonIgnore private Map<String, Object> properties;

  @JsonAnySetter
  public void setProperty(String propertyKey, Object value) {
    if (this.properties == null) {
      this.properties = new HashMap<>();
    }

    this.properties.put(propertyKey, value);
  }

  @JsonAnyGetter
  public Map<String, Object> getProperty() {
    return properties;
  }
}

