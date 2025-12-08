package com.raven.thunder.admin.io.response;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusWiseCount {
  private AtomicInteger draft;
  private AtomicInteger paused;
  private AtomicInteger live;
  private AtomicInteger scheduled;
  private AtomicInteger concluded;
  private AtomicInteger terminated;
}
