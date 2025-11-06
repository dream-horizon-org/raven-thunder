package com.dream11.thunder.core.dao.nudge;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Value;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Statement;
import com.dream11.thunder.core.client.AerospikeClient;
import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.thunder.core.dao.AerospikeRepository;
import com.dream11.thunder.core.dao.NudgeRepository;
import com.dream11.thunder.core.model.Nudge;
import com.dream11.thunder.core.util.ParseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import com.google.inject.Inject;
import lombok.SneakyThrows;

public class NudgeRepositoryImpl extends AerospikeRepository implements NudgeRepository {

  private final String namespace;

  private final WritePolicy createWritePolicy = new WritePolicy();

  private final WritePolicy updateWritePolicy = new WritePolicy();
  private final NudgeRecordMapper nudgeRecordMapper = new NudgeRecordMapper();

  @Inject
  public NudgeRepositoryImpl(AerospikeConfig config, AerospikeClient client) {
    super(config, client);
    this.namespace = config.getAdminDataNamespace();

    setDefaultWritePolicyParams(createWritePolicy, config);
    createWritePolicy.sendKey = true;
    createWritePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;

    setDefaultWritePolicyParams(updateWritePolicy, config);
    updateWritePolicy.sendKey = true;
    updateWritePolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
  }

  @Override
  public Maybe<Nudge> find(String tenantId, String id) {
    Key key = new Key(namespace, Schema.SET, tenantId + ":" + id);
    return find(key, nudgeRecordMapper, Schema.TEMPLATE_BIN);
  }

  @Override
  public Single<Map<String, Nudge>> findAll() {
    Statement query = new Statement();
    query.setNamespace(namespace);
    query.setSetName(Schema.SET);
    query.setBinNames(Schema.TEMPLATE_BIN);
    return findAll(query, keyRecord -> nudgeRecordMapper.apply(keyRecord.record));
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable create(String tenantId, Nudge template) {
    Key key = new Key(namespace, Schema.SET, tenantId + ":" + template.getId());
    Bin tenantBin = new Bin(Schema.TENANT_BIN, tenantId); // enables secondary index
    Bin templateBin =
        new Bin(Schema.TEMPLATE_BIN, new Value.StringValue(ParseUtil.writeValueAsString(template)));
    return upsert(createWritePolicy, key, tenantBin, templateBin).ignoreElement();
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public Completable update(String tenantId, Nudge template) {
    Key key = new Key(namespace, Schema.SET, tenantId + ":" + template.getId());
    Bin tenantBin = new Bin(Schema.TENANT_BIN, tenantId); // enables secondary index
    Bin templateBin =
        new Bin(Schema.TEMPLATE_BIN, new Value.StringValue(ParseUtil.writeValueAsString(template)));
    return upsert(updateWritePolicy, key, tenantBin, templateBin).ignoreElement();
  }
}

