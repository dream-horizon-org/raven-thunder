package com.raven.thunder.core.dao.event;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Value;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.Statement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.raven.thunder.core.client.AerospikeClient;
import com.raven.thunder.core.config.AerospikeConfig;
import com.raven.thunder.core.dao.AerospikeRepository;
import com.raven.thunder.core.dao.EventRepository;
import com.raven.thunder.core.model.EventData;
import com.raven.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventRepositoryImpl extends AerospikeRepository implements EventRepository {

  private final String namespace;
  private final WritePolicy writePolicy = new WritePolicy();
  private final WritePolicy deletePolicy = new WritePolicy();
  private final EventRecordMapper eventRecordMapper = new EventRecordMapper();

  @Inject
  public EventRepositoryImpl(AerospikeConfig config, AerospikeClient client) {
    super(config, client);
    this.namespace = config.getAdminDataNamespace();

    setDefaultWritePolicyParams(writePolicy, config);
    writePolicy.sendKey = true;

    setDefaultWritePolicyParams(deletePolicy, config);
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable upsert(String tenantId, String eventName, EventData eventData) {
    String primaryKey = tenantId + ":" + eventName;
    Key key = new Key(namespace, Schema.SET, primaryKey);

    Bin tenantBin = new Bin(Schema.TENANT_BIN, tenantId);
    Bin eventNameBin = new Bin(Schema.EVENT_NAME_BIN, eventName);
    Bin eventDataBin =
        new Bin(
            Schema.EVENT_DATA_BIN, new Value.StringValue(ParseUtil.writeValueAsString(eventData)));

    return upsert(writePolicy, key, tenantBin, eventNameBin, eventDataBin).ignoreElement();
  }

  @Override
  public Single<List<EventData>> findAllByTenant(String tenantId) {
    Statement query = new Statement();
    query.setNamespace(namespace);
    query.setSetName(Schema.SET);
    query.setIndexName(Schema.TENANT_ID_INDEX);
    query.setFilter(Filter.equal(Schema.TENANT_BIN, tenantId));
    query.setBinNames(Schema.EVENT_DATA_BIN, Schema.TENANT_BIN, Schema.EVENT_NAME_BIN);

    log.info("Fetching all events for tenant: {}", tenantId);
    return findAll(query)
        .map(
            keyRecords ->
                keyRecords.stream()
                    .map(
                        keyRecord -> {
                          try {
                            return eventRecordMapper.apply(keyRecord.record);
                          } catch (Exception e) {
                            log.error("Error mapping event record for tenant: {}", tenantId, e);
                            return null;
                          }
                        })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()))
        .doOnSuccess(events -> log.info("Found {} events for tenant: {}", events.size(), tenantId))
        .doOnError(error -> log.error("Error fetching events for tenant: {}", tenantId, error));
  }

  @Override
  public Maybe<EventData> findByTenantAndEventName(String tenantId, String eventName) {
    String primaryKey = tenantId + ":" + eventName;
    Key key = new Key(namespace, Schema.SET, primaryKey);
    return find(
            key, eventRecordMapper, Schema.EVENT_DATA_BIN, Schema.TENANT_BIN, Schema.EVENT_NAME_BIN)
        .doOnError(
            error ->
                log.error("Error fetching event: {} for tenant: {}", eventName, tenantId, error));
  }

  @Override
  public Single<List<String>> findAllEventNamesByTenant(String tenantId) {
    Statement query = new Statement();
    query.setNamespace(namespace);
    query.setSetName(Schema.SET);
    query.setIndexName(Schema.TENANT_ID_INDEX);
    query.setFilter(Filter.equal(Schema.TENANT_BIN, tenantId));
    query.setBinNames(Schema.EVENT_NAME_BIN); // Only fetch event_name bin for efficiency

    log.info("Fetching all event names for tenant: {}", tenantId);
    return findAll(query)
        .map(
            keyRecords ->
                keyRecords.stream()
                    .map(
                        keyRecord -> {
                          Object eventName = keyRecord.record.bins.get(Schema.EVENT_NAME_BIN);
                          return eventName != null ? eventName.toString() : null;
                        })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()))
        .doOnSuccess(
            eventNames ->
                log.info("Found {} event names for tenant: {}", eventNames.size(), tenantId))
        .doOnError(
            error -> log.error("Error fetching event names for tenant: {}", tenantId, error));
  }

  @Override
  public Single<Boolean> delete(String tenantId, String eventName) {
    String primaryKey = tenantId + ":" + eventName;
    Key key = new Key(namespace, Schema.SET, primaryKey);
    return Single.fromCallable(
            () -> {
              boolean existed = client.getClient().exists(deletePolicy, key);
              if (existed) {
                client.getClient().delete(deletePolicy, key);
              }
              return existed;
            })
        .doOnError(
            error ->
                log.error("Error deleting event: {} for tenant: {}", eventName, tenantId, error));
  }
}
