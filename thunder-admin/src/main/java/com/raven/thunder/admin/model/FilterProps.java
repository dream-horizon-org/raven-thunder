package com.raven.thunder.admin.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterProps {
  private String name;
  private String searchName;
  private String status;
  private String createdBy;
  private List<String> tags;
  private List<String> teams;
  private String behaviourTag;
}
