package com.dream11.thunder.api.service;

import io.reactivex.rxjava3.Single;
import java.util.Set;

public interface UserCohortsClient {

  Single<Set<String>> findAllCohorts(Long userId);
}
