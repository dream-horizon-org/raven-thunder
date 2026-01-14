package com.raven.thunder.admin.io.request;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPatchRequest {
  @NotEmpty @Valid @NotNull private List<EventPropertyInput> properties;
}
