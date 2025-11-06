package com.dream11.thunder.api.dao.statemachine;

import com.dream11.thunder.api.model.StateMachineSnapshot;

interface Schema {

  String SET = "state-machine";

  /** {@link java.util.Map<String, StateMachineSnapshot >} */
  String SNAPSHOT_BIN = "snapshot";
}
