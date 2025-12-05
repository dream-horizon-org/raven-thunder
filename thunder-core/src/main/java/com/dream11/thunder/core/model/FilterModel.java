package com.dream11.thunder.core.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterModel {
  private String name;
  private List<String> tags;
  private String createdBy;
  private String createdOn;
  private List<String> teams;
  private CTAStatus ctaStatus;
}
