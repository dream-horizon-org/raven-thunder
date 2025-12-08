package com.raven.thunder.core.io.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterResponse {
  private List<String> names;
  private List<String> tags;
  private List<String> teams;
  private List<String> createdBy;
}
