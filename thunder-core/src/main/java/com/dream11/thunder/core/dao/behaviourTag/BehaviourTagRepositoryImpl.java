package com.dream11.thunder.core.dao.behaviourTag;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Value;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.Statement;
import com.dream11.thunder.core.client.AerospikeClient;
import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.thunder.core.dao.AerospikeRepository;
import com.dream11.thunder.core.dao.BehaviourTagsRepository;
import com.dream11.thunder.core.model.BehaviourTag;
import com.dream11.thunder.core.util.ParseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.Map;
import com.google.inject.Inject;
import lombok.SneakyThrows;

public class BehaviourTagRepositoryImpl extends AerospikeRepository
    implements BehaviourTagsRepository {

  private final String namespace;
  private final BehaviourTagRecordMapper behaviourTagRecordMapper = new BehaviourTagRecordMapper();
  private final WritePolicy createWritePolicy = new WritePolicy();
  private final WritePolicy updateWritePolicy = new WritePolicy();
  private final Policy defaultReadPolicy = new Policy();
  private final WritePolicy defaultWritePolicy = new WritePolicy();

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

    setDefaultReadPolicyParams(defaultReadPolicy, config);
    defaultReadPolicy.sendKey = true;

    setDefaultWritePolicyParams(defaultWritePolicy, config);
    defaultWritePolicy.sendKey = true;
  }

  @Override
  public Single<Long> generatedIncrementId(String tenantId) {
    Bin incrementCounter = new Bin(Schema.COUNTER_BIN, 1);
    Key globalKey = new Key(namespace, Schema.BEHAVIOUR_TAG_COUNTER, Schema.GLOBAL_BEHAVIOUR_TAG_KEY);
    Key tenantKey = new Key(namespace, Schema.BEHAVIOUR_TAG_COUNTER, tenantId);
    Operation[] operations = new Operation[2];
    operations[0] = Operation.add(incrementCounter);
    operations[1] = Operation.get(Schema.COUNTER_BIN);
    Single<Long> globalValueSingle =
        operate(defaultWritePolicy, globalKey, operations)
            .map(record -> (Long) record.bins.get(Schema.COUNTER_BIN));
    Single<Long> tenantValueSingle =
        operate(defaultWritePolicy, tenantKey, operations)
            .map(record -> (Long) record.bins.get(Schema.COUNTER_BIN));
    return Single.zip(globalValueSingle, tenantValueSingle, (global, ignore) -> global);
  }

  @Override
  public Single<Map<String, BehaviourTag>> findAll() {
    Statement query = new Statement();
    query.setSetName(Schema.SET);
    query.setNamespace(this.namespace);
    query.setBinNames(
        Schema.ID_BIN,
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
        Schema.ID_BIN,
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
  public Maybe<BehaviourTag> find(String tenantId, Long id) {
    return find(
        defaultReadPolicy,
        new Key(namespace, Schema.SET, id),
        behaviourTagRecordMapper,
        Schema.ID_BIN,
        Schema.NAME_BIN,
        Schema.DESCRIPTION_BIN,
        Schema.LINKED_CTAS,
        Schema.EXPOSURE_RULE_BIN,
        Schema.CTA_RELATION_BIN,
        Schema.CREATED_AT_BIN,
        Schema.CREATED_BY_BIN,
        Schema.LAST_UPDATED_AT_BIN,
        Schema.LAST_UPDATED_BY_BIN,
        Schema.TENANT_BIN)
        .filter(behaviourTag -> tenantId.equals(behaviourTag.getTenantId()));
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable create(String tenantId, BehaviourTag behaviourTag) {
    Bin idBin = new Bin(Schema.ID_BIN, new Value.LongValue(behaviourTag.getId()));
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
            new Key(namespace, Schema.SET, behaviourTag.getId()),
            idBin,
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
  public Completable update(String tenantId, Long id, BehaviourTag behaviourTag) {

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
            new Key(namespace, Schema.SET, id),
            descriptionBin,
            exposureRuleBin,
            ctaRelationBin,
            udpatedByRuleBin,
            updatedAtRuleBin,
            linkedCtasBin)
        .ignoreElement();
  }
}

