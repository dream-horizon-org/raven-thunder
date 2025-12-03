package com.dream11.thunder.admin.io.response;

import com.dream11.thunder.core.model.CTA;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CTAListResponse {
  public List<CTA> ctas;
  private int totalEntries;
  private int totalPages;
  private int pageNumber;
  private int pageSize;
  private StatusWiseCount statusWiseCount;
}
