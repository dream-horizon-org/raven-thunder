package com.dream11.thunder.core.dao.cta;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Value;
import com.aerospike.client.cdt.ListOperation;
import com.aerospike.client.cdt.ListOrder;
import com.aerospike.client.cdt.ListPolicy;
import com.aerospike.client.cdt.ListWriteFlags;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.Statement;
import com.dream11.thunder.core.client.AerospikeClient;
import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.thunder.core.dao.AerospikeRepository;
import com.dream11.thunder.core.dao.CTARepository;
import com.dream11.thunder.core.io.response.FilterResponse;
import com.dream11.thunder.core.model.CTA;
import com.dream11.thunder.core.model.CTAStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;

public class CTARepositoryImpl extends AerospikeRepository implements CTARepository {

  private final String namespace;

  String CTA_COUNTER = "cta_counter";
  String META_RECORD = "meta_record";
  private final CTARecordMapper ctaRecordMapper = new CTARecordMapper();
  private final CTADetailsRecordMapper ctaDetailsRecordMapper = new CTADetailsRecordMapper();
  private final ScheduledCTARecordMapper scheduledCTARecordMapper = new ScheduledCTARecordMapper();
  private final ActiveCTARecordMapper activeCTARecordMapper = new ActiveCTARecordMapper();
  private final FilterRecordMapper filterRecordMapper = new FilterRecordMapper();
  private final CreateCTAHelper createCTAHelper = new CreateCTAHelper();
  private final UpdateCTAHelper updateCTAHelper = new UpdateCTAHelper();
  private final WritePolicy createWritePolicy = new WritePolicy();
  private final WritePolicy updateWritePolicy = new WritePolicy();
  private final WritePolicy updateOnlyPolicy = new WritePolicy();
  private final Policy defaultReadPolicy = new Policy();

  @Inject
  public CTARepositoryImpl(AerospikeConfig config, AerospikeClient client) {
    super(config, client);

    this.namespace = config.getAdminDataNamespace();

    setDefaultWritePolicyParams(createWritePolicy, config);
    createWritePolicy.sendKey = true;
    createWritePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;

    setDefaultWritePolicyParams(updateWritePolicy, config);
    updateWritePolicy.sendKey = true;
    updateWritePolicy.recordExistsAction = RecordExistsAction.UPDATE;

    setDefaultWritePolicyParams(updateOnlyPolicy, config);
    updateOnlyPolicy.sendKey = true;
    updateOnlyPolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;

    setDefaultReadPolicyParams(defaultReadPolicy, config);
    defaultReadPolicy.sendKey = true;
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable create(String tenantId, CTA cta) {
    return upsert(
            createWritePolicy,
            new Key(namespace, Schema.SET, cta.getId()),
            createCTAHelper.apply(tenantId, cta))
        .ignoreElement();
  }

  @Override
  public Single<Long> generatedIncrementId(String tenantId) {
    Bin incrementCounter = new Bin(Schema.COUNTER_BIN, 1);
    Key globalKey = new Key(namespace, Schema.CTA_COUNTER, Schema.GLOBAL_CTA_KEY);
    Key tenantKey = new Key(namespace, Schema.CTA_COUNTER, tenantId);
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
  public Maybe<CTA> find(String tenantId, Long id) {
    return find(
            defaultReadPolicy,
            new Key(namespace, Schema.SET, id),
            ctaRecordMapper,
            Schema.RULE_BIN,
            Schema.ID_BIN,
            Schema.STATUS_BIN,
            Schema.NAME_BIN,
            Schema.DESCRIPTION_BIN,
            Schema.TAGS_BIN,
            Schema.TEAM_BIN,
            Schema.BEHAVIOUR_TAG_BIN,
            Schema.START_TIME_BIN,
            Schema.END_TIME_BIN,
            Schema.CREATED_AT_BIN,
            Schema.CREATED_BY_BIN,
            Schema.LAST_UPDATED_AT_BIN,
            Schema.LAST_UPDATED_BY_BIN,
            Schema.TENANT_BIN)
        .filter(cta -> tenantId.equals(cta.getTenantId()));
  }

  @Override
  public Maybe<CTADetails> findWithGeneration(String tenantId, Long id) {
    return find(
            defaultReadPolicy,
            new Key(namespace, Schema.SET, id),
            ctaDetailsRecordMapper,
            Schema.RULE_BIN,
            Schema.ID_BIN,
            Schema.STATUS_BIN,
            Schema.NAME_BIN,
            Schema.DESCRIPTION_BIN,
            Schema.TAGS_BIN,
            Schema.TEAM_BIN,
            Schema.BEHAVIOUR_TAG_BIN,
            Schema.START_TIME_BIN,
            Schema.END_TIME_BIN,
            Schema.CREATED_AT_BIN,
            Schema.CREATED_BY_BIN,
            Schema.LAST_UPDATED_AT_BIN,
            Schema.LAST_UPDATED_BY_BIN,
            Schema.TENANT_BIN)
        .filter(ctaDetails -> tenantId.equals(ctaDetails.getTenantId()));
  }

  @Override
  public Single<Map<Long, CTA>> findAll(String tenantId) {
    Statement query = new Statement();
    query.setNamespace(namespace);
    query.setSetName(Schema.SET);
    query.setIndexName(Schema.STATUS_BIN);
    query.setFilter(Filter.equal(Schema.TENANT_BIN, tenantId));
    query.setBinNames(
        Schema.STATUS_BIN,
        Schema.ID_BIN,
        Schema.RULE_BIN,
        Schema.NAME_BIN,
        Schema.DESCRIPTION_BIN,
        Schema.TAGS_BIN,
        Schema.TEAM_BIN,
        Schema.BEHAVIOUR_TAG_BIN,
        Schema.START_TIME_BIN,
        Schema.END_TIME_BIN,
        Schema.CREATED_AT_BIN,
        Schema.CREATED_BY_BIN,
        Schema.LAST_UPDATED_AT_BIN,
        Schema.LAST_UPDATED_BY_BIN,
        Schema.TENANT_BIN);
    return findAll(query, keyRecord -> ctaRecordMapper.apply(keyRecord.record));
  }

  @Override
  public Single<Map<Long, CTA>> findAllWithStatusActive() {
    Statement query = new Statement();
    query.setSetName(Schema.SET);
    query.setIndexName(Schema.STATUS_BIN);
    query.setNamespace(this.namespace);
    query.setFilter(Filter.equal(Schema.STATUS_BIN, CTAStatus.LIVE.name()));
    query.setBinNames(Schema.RULE_BIN, Schema.ID_BIN, Schema.BEHAVIOUR_TAG_BIN, Schema.TENANT_BIN);
    return findAll(query, keyRecord -> ctaRecordMapper.apply(keyRecord.record))
        .map(
            it -> {
              HashMap<Long, CTA> map = new HashMap<>();
              it.forEach(
                  (k, v) -> {
                    map.put(Long.parseLong(k.toString()), v);
                  });
              return map;
            });
  }

  @Override
  public Single<Map<Long, CTA>> findAllWithStatusPaused() {
    Statement query = new Statement();
    query.setSetName(Schema.SET);
    query.setIndexName(Schema.STATUS_BIN);
    query.setNamespace(this.namespace);
    query.setFilter(Filter.equal(Schema.STATUS_BIN, CTAStatus.PAUSED.name()));
    query.setBinNames(Schema.RULE_BIN, Schema.BEHAVIOUR_TAG_BIN, Schema.TENANT_BIN);
    return findAll(query, keyRecord -> ctaRecordMapper.apply(keyRecord.record))
        .map(
            it -> {
              HashMap<Long, CTA> map = new HashMap<>();
              it.forEach(
                  (k, v) -> {
                    map.put(Long.parseLong(k.toString()), v);
                  });
              return map;
            });
  }

  @Override
  public Single<Map<Long, ActiveCTA>> findAllIdsWithStatusLive() {
    Statement query = new Statement();
    query.setNamespace(namespace);
    query.setSetName(Schema.SET);
    query.setIndexName(Schema.STATUS_BIN);
    query.setFilter(Filter.equal(Schema.STATUS_BIN, CTAStatus.LIVE.name()));
    return findAll(query, activeCTARecordMapper);
  }

  @Override
  public Single<Map<Long, ActiveCTA>> findAllIdsWithStatusPaused() {
    Statement query = new Statement();
    query.setNamespace(namespace);
    query.setSetName(Schema.SET);
    query.setIndexName(Schema.STATUS_BIN);
    query.setFilter(Filter.equal(Schema.STATUS_BIN, CTAStatus.PAUSED.name()));
    return findAll(query, activeCTARecordMapper);
  }

  @Override
  public Maybe<FilterResponse> findFilters(String tenantId) {
    return find(
        new Key(namespace, Schema.META_SET, tenantId),
        filterRecordMapper,
        Schema.TAGS_META_BIN,
        Schema.TEAM_META_BIN,
        Schema.CREATED_BY_META_BIN,
        Schema.NAME_META_BIN);
  }

  @Override
  public Single<Map<Long, ScheduledCTA>> findAllWithStatusScheduled() {
    Statement query = new Statement();
    query.setNamespace(namespace);
    query.setSetName(Schema.SET);
    query.setIndexName(Schema.STATUS_BIN);
    query.setFilter(Filter.equal(Schema.STATUS_BIN, CTAStatus.SCHEDULED.name()));
    return findAll(query, scheduledCTARecordMapper);
  }

  @Override
  public Completable update(Long id, CTAStatus status, Long startTime, Long endTime) {
    return updateStatus(id, status, startTime, endTime);
  }

  @Override
  public Completable update(Long id, CTAStatus status) {
    Bin statusBin = new Bin(Schema.STATUS_BIN, new Value.StringValue(status.name()));
    return upsert(updateOnlyPolicy, new Key(namespace, Schema.SET, id), statusBin).ignoreElement();
  }

  @Override
  public Completable update(Long id, int generation, CTAStatus status) {
    WritePolicy updateWithGenerationWritePolicy = new WritePolicy();
    setDefaultWritePolicyParams(updateWithGenerationWritePolicy, config);
    updateWithGenerationWritePolicy.sendKey = true;
    updateWithGenerationWritePolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
    updateWithGenerationWritePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
    updateWithGenerationWritePolicy.generation = generation;
    Bin statusBin = new Bin(Schema.STATUS_BIN, new Value.StringValue(status.name()));
    return upsert(updateWithGenerationWritePolicy, new Key(namespace, Schema.SET, id), statusBin)
        .ignoreElement();
  }

  @Override
  public Completable update(Long id, List<String> behaviourTag) {
    Bin behaviourTagBin = new Bin(Schema.BEHAVIOUR_TAG_BIN, new Value.ListValue(behaviourTag));
    return upsert(updateWritePolicy, new Key(namespace, Schema.SET, id), behaviourTagBin)
        .ignoreElement();
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable update(CTA cta, int generation) {
    WritePolicy updateWithGenerationWritePolicy = new WritePolicy();
    setDefaultWritePolicyParams(updateWithGenerationWritePolicy, config);
    updateWithGenerationWritePolicy.sendKey = true;
    updateWithGenerationWritePolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
    updateWithGenerationWritePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
    updateWithGenerationWritePolicy.generation = generation;

    return upsert(
            updateWithGenerationWritePolicy,
            new Key(namespace, Schema.SET, cta.getId()),
            updateCTAHelper.apply(cta))
        .ignoreElement();
  }

  @Override
  public Completable terminateOrConclude(Long id, CTAStatus status, Long startTime, Long endTime) {
    return updateStatus(id, status, startTime, endTime);
  }

  private Completable updateStatus(Long id, CTAStatus status, Long startTime, Long endTime) {
    Bin statusBin = new Bin(Schema.STATUS_BIN, new Value.StringValue(status.name()));
    Bin startTimeBin = new Bin(Schema.START_TIME_BIN, new Value.LongValue(startTime));
    Bin endTimeBin = new Bin(Schema.END_TIME_BIN, new Value.LongValue(endTime));
    return upsert(
            updateOnlyPolicy,
            new Key(namespace, Schema.SET, id),
            statusBin,
            startTimeBin,
            endTimeBin)
        .ignoreElement();
  }

  @Override
  public Completable terminateOrConclude(Long id, CTAStatus status, Long endTime) {
    Bin statusBin = new Bin(Schema.STATUS_BIN, new Value.StringValue(status.name()));
    Bin endTimeBin = new Bin(Schema.END_TIME_BIN, new Value.LongValue(endTime));
    return upsert(updateOnlyPolicy, new Key(namespace, Schema.SET, id), statusBin, endTimeBin)
        .ignoreElement();
  }

  @Override
  public Completable updateFilters(
      String tenantId, List<String> tags, String team, String name, String createdBy) {
    int opsSize = 4;
    if (createdBy == null) {
      opsSize = 3;
    }
    int listWriteFlags =
        ListWriteFlags.ADD_UNIQUE | ListWriteFlags.PARTIAL | ListWriteFlags.NO_FAIL;
    ListPolicy listPolicy = new ListPolicy(ListOrder.UNORDERED, listWriteFlags);

    Key tagsKey = new Key(namespace, Schema.META_SET, tenantId);

    List<Value> valueList = new ArrayList<>();
    tags.forEach(it -> valueList.add(new Value.StringValue(it)));

    Operation[] operations = new Operation[opsSize];
    operations[0] = ListOperation.appendItems(listPolicy, Schema.TAGS_META_BIN, valueList);
    operations[1] =
        ListOperation.append(listPolicy, Schema.TEAM_META_BIN, new Value.StringValue(team));

    operations[2] =
        ListOperation.append(listPolicy, Schema.NAME_META_BIN, new Value.StringValue(name));
    if (createdBy != null) {
      operations[3] =
          ListOperation.append(
              listPolicy, Schema.CREATED_BY_META_BIN, new Value.StringValue(createdBy));
    }

    return operate(defaultWritePolicy, tagsKey, operations).ignoreElement();
  }
}
