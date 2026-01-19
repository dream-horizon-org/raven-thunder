package com.raven.thunder.core.dao;

import com.raven.thunder.core.model.TestCTA;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;

public interface TestCTARepository {

  Completable create(String tenantId, TestCTA cta);

  Maybe<TestCTA> find(String tenantId, Long id);

  Single<Boolean> delete(Long id);

  Single<Map<String, List<TestCTA>>> fetchAllTestCTAs();

  Single<Map<Long, TestCTA>> findAll();
}
