package com.raven.thunder.core.dao.testCta;

import com.aerospike.client.Key;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Statement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.raven.thunder.core.client.AerospikeClient;
import com.raven.thunder.core.config.AerospikeConfig;
import com.raven.thunder.core.dao.AerospikeRepository;
import com.raven.thunder.core.dao.TestCTARepository;
import com.raven.thunder.core.model.TestCTA;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;

public class TestCTARepositoryImpl extends AerospikeRepository implements TestCTARepository {

  private final String namespace;

  private final CreateTestCTAHelper createTestCTAHelper = new CreateTestCTAHelper();
  private final TestCTARecordMapper testCTARecordMapper = new TestCTARecordMapper();
  private final WritePolicy createWritePolicy = new WritePolicy();

  // no update/delete-specific policies needed for now

  @Inject
  public TestCTARepositoryImpl(AerospikeConfig config, AerospikeClient client) {
    super(config, client);

    this.namespace = config.getAdminDataNamespace();

    setDefaultWritePolicyParams(createWritePolicy, config);
    createWritePolicy.sendKey = true;
    createWritePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable create(String tenantId, TestCTA testCTA) {
    WritePolicy writePolicy = new WritePolicy();
    setDefaultWritePolicyParams(writePolicy, config);
    writePolicy.sendKey = true;
    writePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;
    // Set expiration in seconds (TTL from current time)
    // Default to 30 minutes (1800 seconds) if expiresInMinutes is 0
    long expiresInSeconds =
        testCTA.getExpiresInMinutes() > 0 ? testCTA.getExpiresInMinutes() * 60L : 30 * 60L;
    writePolicy.expiration = Math.toIntExact(expiresInSeconds);
    return upsert(
            writePolicy,
            new Key(namespace, Schema.SET, testCTA.getId()),
            createTestCTAHelper.apply(tenantId, testCTA))
        .ignoreElement();
  }

  @Override
  public Single<Boolean> delete(Long ctaId) {
    Key key = new Key(namespace, Schema.SET, ctaId);
    WritePolicy deletePolicy = new WritePolicy();
    setDefaultWritePolicyParams(deletePolicy, config);
    return client.rxDelete(deletePolicy, key);
  }

  @Override
  public Maybe<TestCTA> find(String tenantId, Long id) {
    return find(
            defaultReadPolicy,
            new Key(namespace, Schema.SET, id),
            testCTARecordMapper,
            Schema.RULE_BIN,
            Schema.ID_BIN,
            Schema.CREATED_AT_BIN,
            Schema.EXPIRES_AT_BIN,
            Schema.CREATED_BY_BIN,
            Schema.TENANT_BIN,
            Schema.USER_IDS_BIN)
        .filter(testCta -> tenantId.equals(testCta.getTenantId()));
  }

  @Override
  public Single<Map<Long, TestCTA>> findAll() {
    Statement query = new Statement();
    query.setNamespace(namespace);
    query.setSetName(Schema.SET);
    query.setBinNames(
        Schema.RULE_BIN,
        Schema.ID_BIN,
        Schema.CREATED_AT_BIN,
        Schema.EXPIRES_AT_BIN,
        Schema.CREATED_BY_BIN,
        Schema.TENANT_BIN,
        Schema.USER_IDS_BIN);
    return findAll(query, keyRecord -> testCTARecordMapper.apply(keyRecord.record))
        .map(
            it -> {
              Map<Long, TestCTA> map = new HashMap<>();
              it.forEach(
                  (k, v) -> {
                    map.put(Long.parseLong(k.toString()), v);
                  });
              return map;
            })
        .onErrorReturn(
            throwable -> {
              // Handle missing namespace gracefully (e.g., in test environments)
              // Return empty map if namespace doesn't exist
              return new HashMap<>();
            });
  }

  @Override
  public Single<Map<String, List<TestCTA>>> fetchAllTestCTAs() {
    return findAll()
        .map(
            all -> {
              Map<String, List<TestCTA>> result = new HashMap<>();
              all.values()
                  .forEach(
                      testCta -> {
                        if (testCta.getUserIds() != null) {
                          for (Long userId : testCta.getUserIds()) {
                            String key = testCta.getTenantId() + ":" + userId;
                            result.computeIfAbsent(key, k -> new ArrayList<>()).add(testCta);
                          }
                        }
                      });
              return result;
            });
  }
}
