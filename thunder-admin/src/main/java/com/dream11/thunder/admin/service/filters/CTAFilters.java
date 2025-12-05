package com.dream11.thunder.admin.service.filters;

import com.dream11.thunder.admin.model.FilterProps;
import com.dream11.thunder.core.model.CTA;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CTAFilters {

  public static Predicate<CTA> filter(FilterProps filterProps) {
    return Stream.of(
            filterName(filterProps.getName()),
            filterSearchName(filterProps.getSearchName()),
            filterCreatedBy(filterProps.getCreatedBy()),
            filterTeams(filterProps.getTeams()),
            filterTags(filterProps.getTags()),
            filterBehaviourTag(filterProps.getBehaviourTag()))
        .reduce(x -> true, Predicate::and);
  }

  private static Predicate<CTA> filterName(String name) {
    return cta -> {
      if (name == null) {
        return true;
      }
      return cta.getName().equals(name);
    };
  }

  private static Predicate<CTA> filterSearchName(String searchName) {
    return cta -> {
      if (searchName == null || searchName.isEmpty()) {
        return true;
      }
      return cta.getName().contains(searchName);
    };
  }

  private static Predicate<CTA> filterCreatedBy(String createdBy) {
    return cta -> {
      if (createdBy == null) {
        return true;
      }
      return cta.getCreatedBy().equals(createdBy);
    };
  }

  private static Predicate<CTA> filterTeams(List<String> teams) {
    return cta -> {
      if (teams == null || teams.isEmpty()) {
        return true;
      }
      return teams.contains(cta.getTeam());
    };
  }

  private static Predicate<CTA> filterTags(List<String> tags) {
    return cta -> {
      if (tags == null || tags.isEmpty()) {
        return true;
      }
      for (String tag : cta.getTags()) {
        if (tags.contains(tag)) {
          return true;
        }
      }
      return false;
    };
  }

  private static Predicate<CTA> filterBehaviourTag(String behaviourTag) {
    return cta -> {
      if (behaviourTag == null) {
        return true;
      }
      return cta.getBehaviourTags() != null && cta.getBehaviourTags().contains(behaviourTag);
    };
  }
}
