package com.dream11.thunder.core.model.rule;

import com.dream11.thunder.core.pojo.JsonTemplate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateTransitionCondition {

  @NotNull private String transitionTo;
  @NotNull @Valid private Filters filters;

  @Data
  public static class Filters {

    @NotNull private String operator;
    @NotNull @Valid private List<Object> filter;
  }

  @Data
  public static class Filter {

    @NotNull private String propertyName;
    @NotNull private String propertyType;
    @NotNull private String comparisonType;
    @NotNull private Object comparisonValue;
    private JsonTemplate functions;
  }

  @Data
  public static class FunctionFilter {

    @NotNull private String funProp1;
    @NotNull private String funProp2;
    @NotNull private String functionOperator;
    @NotNull private String comparisonType;
    @NotNull private Object comparisonValue;
    private JsonTemplate functions;
  }
}

