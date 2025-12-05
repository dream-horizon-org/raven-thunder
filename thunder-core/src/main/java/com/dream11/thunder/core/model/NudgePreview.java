package com.dream11.thunder.core.model;

import static com.dream11.thunder.core.error.ServiceError.NUDGE_PREVIEW_ID_CANNOT_BE_EMPTY;
import static com.dream11.thunder.core.error.ServiceError.NUDGE_PREVIEW_TEMPLATE_CANNOT_BE_EMPTY;

import com.dream11.thunder.core.exception.ThunderException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NudgePreview {
  private String id;

  private String template;

  private String tenantId;

  // ttl in seconds
  private Integer ttl;

  public void validate() {
    if (id == null || id.isEmpty()) {
      throw new ThunderException(
          NUDGE_PREVIEW_ID_CANNOT_BE_EMPTY.getErrorMessage(),
          NUDGE_PREVIEW_ID_CANNOT_BE_EMPTY.getErrorCode(),
          NUDGE_PREVIEW_ID_CANNOT_BE_EMPTY.getHttpStatusCode());
    }
    if (template == null || template.isEmpty()) {
      throw new ThunderException(
          NUDGE_PREVIEW_TEMPLATE_CANNOT_BE_EMPTY.getErrorMessage(),
          NUDGE_PREVIEW_TEMPLATE_CANNOT_BE_EMPTY.getErrorCode(),
          NUDGE_PREVIEW_TEMPLATE_CANNOT_BE_EMPTY.getHttpStatusCode());
    }
  }
}
