package com.raven.thunder.api.dao.statemachine;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Value;
import com.aerospike.client.policy.WritePolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.raven.thunder.api.dao.StateMachineRepository;
import com.raven.thunder.api.model.UserDataSnapshot;
import com.raven.thunder.core.client.AerospikeClient;
import com.raven.thunder.core.config.AerospikeConfig;
import com.raven.thunder.core.dao.AerospikeRepository;
import com.raven.thunder.core.util.ParseUtil;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StateMachineRepositoryImpl extends AerospikeRepository
    implements StateMachineRepository {

  private final String namespace;

  private final StateMachineRecordMapper stateMachineRecordMapper = new StateMachineRecordMapper();

  /**
   * @see <a
   *     href="https://discuss.aerospike.com/t/faq-what-is-difference-between-update-and-replace/5512">Upsert
   *     Performance</a>
   */
  private final WritePolicy upsertWritePolicy = new WritePolicy();

  @Inject
  public StateMachineRepositoryImpl(AerospikeConfig config, AerospikeClient client) {
    super(config, client);
    this.namespace = config.getUserDataNamespace();

    setDefaultWritePolicyParams(upsertWritePolicy, config);
  }

  @Override
  public Maybe<UserDataSnapshot> find(String tenantId, Long userId) {
    return find(
        new Key(namespace, Schema.SET, tenantId + ":" + userId),
        stateMachineRecordMapper,
        Schema.SNAPSHOT_BIN);
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Single<Boolean> upsert(String tenantId, Long userId, UserDataSnapshot snapshot) {
    Bin snapshotBin =
        new Bin(Schema.SNAPSHOT_BIN, new Value.StringValue(ParseUtil.writeValueAsString(snapshot)));
    return upsert(
            upsertWritePolicy, new Key(namespace, Schema.SET, tenantId + ":" + userId), snapshotBin)
        .map(ignored -> true);
  }
}
