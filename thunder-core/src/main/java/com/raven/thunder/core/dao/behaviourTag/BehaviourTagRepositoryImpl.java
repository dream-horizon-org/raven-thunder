package com.raven.thunder.core.dao.behaviourTag;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Value;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.Statement;
import com.raven.thunder.core.client.AerospikeClient;
import com.raven.thunder.core.config.AerospikeConfig;
import com.raven.thunder.core.dao.AerospikeRepository;
import com.raven.thunder.core.dao.BehaviourTagsRepository;
import com.raven.thunder.core.model.BehaviourTag;
import com.raven.thunder.core.util.ParseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.Map;
import lombok.SneakyThrows;

public class BehaviourTagRepositoryImpl extends AerospikeRepository
    implements BehaviourTagsRepository {

  private final String namespace;
  private final BehaviourTagRecordMapper behaviourTagRecordMapper = new BehaviourTagRecordMapper();
  private final WritePolicy createWritePolicy = new WritePolicy();
  private final WritePolicy updateWritePolicy = new WritePolicy();

  @Inject
  public BehaviourTagRepositoryImpl(AerospikeConfig config, AerospikeClient client) {
    super(config, client);

    this.namespace = config.getAdminDataNamespace();

    // TODO: add generation flag for update policy

    setDefaultWritePolicyParams(createWritePolicy, config);
    createWritePolicy.sendKey = true;
    createWritePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;

    setDefaultWritePolicyParams(updateWritePolicy, config);
    updateWritePolicy.sendKey = true;
    updateWritePolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
  }

  @Override
  public Single<Map<String, BehaviourTag>> findAll() {
    Statement query = new Statement();
    query.setSetName(Schema.SET);
    query.setNamespace(this.namespace);
    query.setBinNames(
        Schema.NAME_BIN,
        Schema.DESCRIPTION_BIN,
        Schema.LINKED_CTAS,
        Schema.EXPOSURE_RULE_BIN,
        Schema.CTA_RELATION_BIN,
        Schema.CREATED_AT_BIN,
        Schema.CREATED_BY_BIN,
        Schema.LAST_UPDATED_AT_BIN,
        Schema.LAST_UPDATED_BY_BIN,
        Schema.TENANT_BIN);
    return findAll(query, keyRecord -> behaviourTagRecordMapper.apply(keyRecord.record));
  }

  @Override
  public Single<Map<String, BehaviourTag>> findAll(String tenantId) {
    Statement query = new Statement();
    query.setSetName(Schema.SET);
    query.setNamespace(this.namespace);
    query.setIndexName(Schema.TENANT_BIN);
    query.setFilter(Filter.equal(Schema.TENANT_BIN, tenantId));
    query.setBinNames(
        Schema.NAME_BIN,
        Schema.DESCRIPTION_BIN,
        Schema.LINKED_CTAS,
        Schema.EXPOSURE_RULE_BIN,
        Schema.CTA_RELATION_BIN,
        Schema.CREATED_AT_BIN,
        Schema.CREATED_BY_BIN,
        Schema.LAST_UPDATED_AT_BIN,
        Schema.LAST_UPDATED_BY_BIN,
        Schema.TENANT_BIN);
    return findAll(query, keyRecord -> behaviourTagRecordMapper.apply(keyRecord.record));
  }

  @Override
  public Maybe<BehaviourTag> find(String tenantId, String behaviourTagName) {
    return find(
        defaultReadPolicy,
        new Key(namespace, Schema.SET, tenantId + ":" + behaviourTagName),
        behaviourTagRecordMapper,
        Schema.NAME_BIN,
        Schema.DESCRIPTION_BIN,
        Schema.LINKED_CTAS,
        Schema.EXPOSURE_RULE_BIN,
        Schema.CTA_RELATION_BIN,
        Schema.CREATED_AT_BIN,
        Schema.CREATED_BY_BIN,
        Schema.LAST_UPDATED_AT_BIN,
        Schema.LAST_UPDATED_BY_BIN,
        Schema.TENANT_BIN);
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable create(String tenantId, BehaviourTag behaviourTag) {
    Bin nameBin = new Bin(Schema.NAME_BIN, new Value.StringValue(behaviourTag.getName()));
    Bin descriptionBin =
        new Bin(Schema.DESCRIPTION_BIN, new Value.StringValue(behaviourTag.getDescription()));
    Bin exposureRuleBin =
        new Bin(
            Schema.EXPOSURE_RULE_BIN,
            new Value.StringValue(ParseUtil.writeValueAsString(behaviourTag.getExposureRule())));
    Bin ctaRelationBin =
        new Bin(
            Schema.CTA_RELATION_BIN,
            new Value.StringValue(ParseUtil.writeValueAsString(behaviourTag.getCtaRelation())));
    Bin createdByRuleBin =
        new Bin(Schema.CREATED_BY_BIN, new Value.StringValue(behaviourTag.getCreatedBy()));
    Bin createdAtRuleBin =
        new Bin(Schema.CREATED_AT_BIN, new Value.LongValue(behaviourTag.getCreatedAt()));
    Bin linkedCtasBin =
        new Bin(
            Schema.LINKED_CTAS, new Value.ListValue(new ArrayList<>(behaviourTag.getLinkedCtas())));
    Bin tenantBin = new Bin(Schema.TENANT_BIN, new Value.StringValue(tenantId));
    return upsert(
            createWritePolicy,
            new Key(namespace, Schema.SET, tenantId + ":" + behaviourTag.getName()),
            nameBin,
            descriptionBin,
            exposureRuleBin,
            ctaRelationBin,
            createdByRuleBin,
            createdAtRuleBin,
            linkedCtasBin,
            tenantBin)
        .ignoreElement();
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable update(String tenantId, String behaviourTagName, BehaviourTag behaviourTag) {

    Bin descriptionBin =
        new Bin(Schema.DESCRIPTION_BIN, new Value.StringValue(behaviourTag.getDescription()));
    Bin exposureRuleBin =
        new Bin(
            Schema.EXPOSURE_RULE_BIN,
            new Value.StringValue(ParseUtil.writeValueAsString(behaviourTag.getExposureRule())));
    Bin ctaRelationBin =
        new Bin(
            Schema.CTA_RELATION_BIN,
            new Value.StringValue(ParseUtil.writeValueAsString(behaviourTag.getCtaRelation())));
    Bin udpatedByRuleBin =
        new Bin(Schema.LAST_UPDATED_BY_BIN, new Value.StringValue(behaviourTag.getLastUpdatedBy()));
    Bin updatedAtRuleBin =
        new Bin(Schema.LAST_UPDATED_AT_BIN, new Value.LongValue(behaviourTag.getLastUpdatedAt()));
    Bin linkedCtasBin =
        new Bin(
            Schema.LINKED_CTAS, new Value.ListValue(new ArrayList<>(behaviourTag.getLinkedCtas())));

    return upsert(
            updateWritePolicy,
            new Key(namespace, Schema.SET, tenantId + ":" + behaviourTagName),
            descriptionBin,
            exposureRuleBin,
            ctaRelationBin,
            udpatedByRuleBin,
            updatedAtRuleBin,
            linkedCtasBin)
        .ignoreElement();
  }
}
