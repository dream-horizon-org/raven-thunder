package com.raven.thunder.api.dao.statemachine;

import com.raven.thunder.api.model.StateMachineSnapshot;

interface Schema {

  String SET = "state-machine";

  /** {@link java.util.Map<String, StateMachineSnapshot >} */
  String SNAPSHOT_BIN = "snapshot";
}
