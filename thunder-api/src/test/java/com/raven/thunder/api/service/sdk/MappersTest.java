package com.raven.thunder.api.service.sdk;

import static org.assertj.core.api.Assertions.assertThat;

import com.raven.thunder.api.io.response.RuleResponse;
import com.raven.thunder.api.model.BehaviourExposureRule;
import com.raven.thunder.api.model.CTARelationSnapshot;
import com.raven.thunder.core.model.CTARelation;
import com.raven.thunder.core.model.ExposureRule;
import com.raven.thunder.core.model.Frequency;
import com.raven.thunder.core.model.rule.LifespanFrequency;
import com.raven.thunder.core.model.rule.Rule;
import com.raven.thunder.core.model.rule.SessionFrequency;
import com.raven.thunder.core.model.rule.WindowFrequency;
import com.raven.thunder.core.model.rule.WindowFrequencyUnit;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MappersTest {

  @Test
  void ruleMapper_mapsAllFields() {
    SessionFrequency session = new SessionFrequency();
    session.setLimit(1);
    WindowFrequency window = new WindowFrequency();
    window.setLimit(1);
    window.setUnit(WindowFrequencyUnit.hours);
    window.setValue(6);
    LifespanFrequency lifespan = new LifespanFrequency();
    lifespan.setLimit(10);
    Frequency frequency = new Frequency();
    frequency.setSession(session);
    frequency.setWindow(window);
    frequency.setLifeSpan(lifespan);

    Rule rule =
        new Rule(
            null,
            Map.of("A", "doA"),
            List.of("reset"),
            true,
            List.of("ctx"),
            Map.of(),
            null,
            5,
            1_000L,
            2_000L,
            List.of(Map.of()),
            frequency);

    RuleMapper mapper = new RuleMapper();
    RuleResponse resp = mapper.apply(rule);
    assertThat(resp.getPriority()).isEqualTo(5);
    assertThat(resp.getCtaValidTill()).isEqualTo(2_000L);
    assertThat(resp.getStateMachineTTL()).isEqualTo(1_000L);
  }

  @Test
  void behaviourExposureRuleMapper_mapsCoreExposureRule() throws Exception {
    ExposureRule er = new ExposureRule();
    SessionFrequency session = new SessionFrequency();
    session.setLimit(1);
    WindowFrequency window = new WindowFrequency();
    window.setLimit(1);
    window.setUnit(WindowFrequencyUnit.days);
    window.setValue(1);
    LifespanFrequency lifespan = new LifespanFrequency();
    lifespan.setLimit(7);
    er.setSession(session);
    er.setWindow(window);
    er.setLifespan(lifespan);

    BehaviourExposureRuleMapper mapper = new BehaviourExposureRuleMapper();
    BehaviourExposureRule out = mapper.apply(er);
    assertThat(out.getSession().getLimit()).isEqualTo(1);
    assertThat(out.getWindow().getUnit()).isEqualTo(WindowFrequencyUnit.days);
    assertThat(out.getLifespan().getLimit()).isEqualTo(7);
  }

  @Test
  void ctaRelationMapper_mapsShownAndHideLists() {
    CTARelation relation = new CTARelation();
    relation.setShownCta(
        new com.raven.thunder.core.model.CtaRelationRule(
            com.raven.thunder.core.model.CtaRelationRuleTypes.LIST, java.util.Set.of("a")));
    relation.setHideCta(
        new com.raven.thunder.core.model.CtaRelationRule(
            com.raven.thunder.core.model.CtaRelationRuleTypes.LIST, java.util.Set.of("b")));

    CTARelationMapper mapper = new CTARelationMapper();
    CTARelationSnapshot snap = mapper.apply(relation);
    assertThat(snap.getShownCta().getCtaList()).containsExactlyInAnyOrder("a");
    assertThat(snap.getHideCta().getCtaList()).containsExactlyInAnyOrder("b");
  }
}
