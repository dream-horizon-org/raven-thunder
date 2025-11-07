package com.dream11.thunder.core.dao.nudge.preview;

import static com.dream11.thunder.core.error.ServiceError.NUDGE_PREVIEW_NOT_FOUND_EXCEPTION;
import static com.dream11.thunder.core.error.ServiceError.TENANT_NOT_AUTHORISED;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import com.dream11.thunder.core.client.AerospikeClient;
import com.dream11.thunder.core.exception.ThunderException;
import com.dream11.thunder.core.config.AerospikeConfig;
import com.dream11.thunder.core.dao.AerospikeRepository;
import com.dream11.thunder.core.dao.NudgePreviewRepository;
import com.dream11.thunder.core.model.NudgePreview;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import com.google.inject.Inject;

public class NudgePreviewRepositoryImpl extends AerospikeRepository
    implements NudgePreviewRepository {

  private final String namespace;
  private final Policy readPolicy = new Policy();
  private final AerospikeConfig config;

  @Inject
  public NudgePreviewRepositoryImpl(AerospikeConfig config, AerospikeClient client) {
    super(config, client);
    this.config = config;
    this.namespace = config.getAdminDataNamespace();
  }

  @Override
  public Completable createOrUpdate(String tenantId, NudgePreview nudgePreview) {
    nudgePreview.setTenantId(tenantId);
    WritePolicy writePolicy = new WritePolicy();
    setDefaultWritePolicyParams(writePolicy, config);
    writePolicy.sendKey = true;
    writePolicy.expiration = nudgePreview.getTtl() == null ? 60 * 60 : nudgePreview.getTtl();

    Bin tenantBin = new Bin(Schema.TENANT_BIN, tenantId);
    Bin templateBin = new Bin(Schema.TEMPLATE_BIN, nudgePreview.getTemplate());
    Key key = new Key(namespace, Schema.SET, nudgePreview.getId());

    return upsert(writePolicy, key, tenantBin, templateBin).ignoreElement();
  }

  @Override
  public Maybe<NudgePreview> find(String tenantId, String id) {
    return find(
            readPolicy, new Key(namespace, Schema.SET, id), Schema.TEMPLATE_BIN, Schema.TENANT_BIN)
        .map(
            (record) -> {
              if (!tenantId.equals(record.bins.get(Schema.TENANT_BIN))) {
                throw new ThunderException(
                    TENANT_NOT_AUTHORISED.getErrorMessage(),
                    TENANT_NOT_AUTHORISED.getErrorCode(),
                    TENANT_NOT_AUTHORISED.getHttpStatusCode());
              }
              if (record.bins.get(Schema.TEMPLATE_BIN) == null) {
                throw new ThunderException(
                    NUDGE_PREVIEW_NOT_FOUND_EXCEPTION.getErrorMessage(),
                    NUDGE_PREVIEW_NOT_FOUND_EXCEPTION.getErrorCode(),
                    NUDGE_PREVIEW_NOT_FOUND_EXCEPTION.getHttpStatusCode());
              }
              NudgePreview nudgePreview = new NudgePreview();
              nudgePreview.setId(id);
              nudgePreview.setTemplate((String) record.bins.get(Schema.TEMPLATE_BIN));
              nudgePreview.setTenantId((String) record.bins.get(Schema.TENANT_BIN));
              return nudgePreview;
            })
        .switchIfEmpty(
            Maybe.error(
                new ThunderException(
                    NUDGE_PREVIEW_NOT_FOUND_EXCEPTION.getErrorMessage(),
                    NUDGE_PREVIEW_NOT_FOUND_EXCEPTION.getErrorCode(),
                    NUDGE_PREVIEW_NOT_FOUND_EXCEPTION.getHttpStatusCode())));
  }
}

