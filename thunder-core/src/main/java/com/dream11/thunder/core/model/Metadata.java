package com.dream11.thunder.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
  private String name;
  private String tags;
  private String description;
  private String createdBy;
  private String createdOn;
  private String teams;
  private String lastEditedBy;
}
