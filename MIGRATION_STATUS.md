# Thunder Migration Status

## Overview
This document tracks the migration of business logic from the internal `thunder` repository to the open-source `thunder-oss` project.

## Completed ✅

### Core Models (`thunder-core/src/main/java/com/dream11/thunder/core/model/`)
- ✅ BehaviourTag.java
- ✅ CTA.java
- ✅ Nudge.java
- ✅ NudgePreview.java
- ✅ CTAStatus.java (enum)
- ✅ Metadata.java
- ✅ FilterModel.java
- ✅ CTARelation.java
- ✅ ExposureRule.java
- ✅ Frequency.java
- ✅ CohortEligibility.java
- ✅ CtaRelationRule.java
- ✅ CtaRelationRuleTypes.java (enum)
- ✅ GroupByConfig.java

### Core Model Rules (`thunder-core/src/main/java/com/dream11/thunder/core/model/rule/`)
- ✅ Rule.java
- ✅ Action.java (abstract)
- ✅ NudgeAction.java
- ✅ SessionFrequency.java
- ✅ WindowFrequency.java
- ✅ WindowFrequencyUnit.java (enum)
- ✅ LifespanFrequency.java
- ✅ StateTransitionCondition.java (with inner classes)

### Core POJOs (`thunder-core/src/main/java/com/dream11/thunder/core/pojo/`)
- ✅ JsonTemplate.java

### Core Configuration (`thunder-core/src/main/java/com/dream11/thunder/core/config/`)
- ✅ Interval.java
- ✅ AerospikeConfig.java (enhanced with Interval helper methods)

### Core Exceptions & Errors (`thunder-core/src/main/java/com/dream11/thunder/core/`)
- ✅ exception/ThunderException.java (custom implementation replacing RestException)
- ✅ error/ServiceError.java (enum)

### Core Utilities (`thunder-core/src/main/java/com/dream11/thunder/core/util/`)
- ✅ ParseUtil.java
- ✅ FormatUtil.java
- ⚠️ ResponseWrapper.java - **SKIPPED** (depends on internal com.dream11.rest library)

### Core Clients (`thunder-core/src/main/java/com/dream11/thunder/core/client/`)
- ✅ AerospikeClient.java (interface - enhanced with reactive operations)
- ✅ AerospikeClientImpl.java (enhanced with rxGet, rxPut, rxQuery, rxOperate methods)

### Core DAOs (`thunder-core/src/main/java/com/dream11/thunder/core/dao/`)
- ✅ AerospikeOperations.java (interface - existing)
- ✅ AerospikeRepository.java (abstract base class)
- ✅ BehaviourTagsRepository.java (interface)
- ✅ CTARepository.java (interface)
- ✅ NudgeRepository.java (interface)
- ✅ NudgePreviewRepository.java (interface)

### DAO Implementations - Nudge
- ✅ dao/nudge/Schema.java
- ✅ dao/nudge/NudgeRecordMapper.java
- ✅ dao/nudge/NudgeRepositoryImpl.java
- ✅ dao/nudge/preview/Schema.java
- ✅ dao/nudge/preview/NudgePreviewRepositoryImpl.java

### DAO Models (`thunder-core/src/main/java/com/dream11/thunder/core/dao/cta/`)
- ✅ cta/ActiveCTA.java
- ✅ cta/CTADetails.java
- ✅ cta/ScheduledCTA.java

### Core IO/Response (`thunder-core/src/main/java/com/dream11/thunder/core/io/response/`)
- ✅ FilterResponse.java

### Dependencies Added
- ✅ javax.validation:validation-api (2.0.1.Final)
- ✅ org.hibernate.validator:hibernate-validator (6.2.5.Final)
- ✅ com.google.code.findbugs:jsr305 (3.0.2) - for @Nullable
- ✅ com.fasterxml.jackson.core:jackson-databind (2.15.2)
- ✅ com.fasterxml.jackson.datatype:jackson-datatype-jsr310 (2.15.2)

## Pending 📋

### DAO Implementations - BehaviourTag
- ✅ dao/behaviourTag/Schema.java
- ✅ dao/behaviourTag/BehaviourTagRecordMapper.java
- ✅ dao/behaviourTag/BehaviourTagRepositoryImpl.java
- ⏳ dao/behaviourTag/BehaviourTagRepositoryModule.java (Guice module - optional)

### DAO Implementations - CTA
- ✅ dao/cta/Schema.java
- ✅ dao/cta/CTARecordMapper.java
- ✅ dao/cta/CTADetailsRecordMapper.java
- ✅ dao/cta/ActiveCTARecordMapper.java
- ✅ dao/cta/ScheduledCTARecordMapper.java
- ✅ dao/cta/FilterRecordMapper.java
- ✅ dao/cta/CreateCTAHelper.java
- ✅ dao/cta/UpdateCTAHelper.java
- ✅ dao/cta/CTARepositoryImpl.java
- ⏳ dao/cta/CTARepositoryModule.java (Guice module - optional)

### Thunder API Module (`thunder-api/`)

#### Configuration
- ⏳ api/config/AppConfig.java
- ⏳ api/config/AppConfigProvider.java
- ⏳ api/config/CacheConfig.java
- ⏳ api/config/ResilienceConfig.java
- ⏳ api/config/ResilienceConfigProvider.java
- ⏳ api/config/UserCohortsConfig.java **[DEPRECATE]**
- ⏳ api/config/VertxTimerConfig.java

#### Constants
- ⏳ api/constant/Constants.java

#### DAOs
- ⏳ api/dao/StateMachineRepository.java
- ⏳ api/dao/statemachine/Schema.java
- ⏳ api/dao/statemachine/StateMachineRecordMapper.java
- ⏳ api/dao/statemachine/StateMachineRepositoryImpl.java
- ⏳ api/dao/statemachine/StateMachineRepositoryModule.java

#### Exceptions & Errors
- ⏳ api/exception/DefinedException.java
- ⏳ api/exception/ErrorEntity.java
- ⏳ api/error/ServiceError.java

#### Models
- ⏳ api/model/BehaviourExposureRule.java
- ⏳ api/model/BehaviourTagSnapshot.java
- ⏳ api/model/CTARelationSnapshot.java
- ⏳ api/model/CTAReset.java
- ⏳ api/model/StateMachine.java
- ⏳ api/model/StateMachineSnapshot.java
- ⏳ api/model/UserDataSnapshot.java

#### Request/Response Objects
- ⏳ api/io/request/CTASnapshotRequest.java
- ⏳ api/io/response/BehaviourTagAndData.java
- ⏳ api/io/response/BehaviourTagsResponse.java
- ⏳ api/io/response/CTAResponse.java
- ⏳ api/io/response/RuleResponse.java
- ⏳ api/io/response/UserCTAAndStateMachineResponse.java

#### Controllers
- ⏳ api/rest/AppDebugController.java
- ⏳ api/rest/HealthCheck.java
- ⏳ api/rest/SdkApiController.java

#### Services
- ⏳ api/service/AppDebugService.java
- ⏳ api/service/SdkService.java
- ⏳ api/service/StaticDataCache.java
- ⏳ api/service/UserCohortsClient.java **[DEPRECATE - return List.of("all")]**

#### Service Implementations
- ⏳ api/service/cache/MasterData.java
- ⏳ api/service/cache/StaticDataCacheImpl.java
- ⏳ api/service/cache/StaticDataCacheModule.java
- ⏳ api/service/cohort/UserCohortsClientImpl.java **[DEPRECATE - return List.of("all")]**
- ⏳ api/service/cohort/UserCohortsModule.java **[DEPRECATE]**
- ⏳ api/service/debug/AppDebugServiceImpl.java
- ⏳ api/service/debug/AppDebugModule.java
- ⏳ api/service/sdk/BehaviourExposureRuleMapper.java
- ⏳ api/service/sdk/BehaviourTagMapper.java
- ⏳ api/service/sdk/CTARelationMapper.java
- ⏳ api/service/sdk/RuleMapper.java
- ⏳ api/service/sdk/SdkServiceImpl.java
- ⏳ api/service/sdk/CtaSdkApiModule.java

#### Utilities
- ⏳ api/util/SDUICircuitBreaker.java

#### Main Application
- ⏳ api/MainApplication.java
- ⏳ api/MainModule.java

### Thunder Admin Module (`thunder-admin/` - formerly `thunder-master/`)

#### Configuration
- ⏳ master/config/AppConfig.java
- ⏳ master/config/AppConfigProvider.java
- ⏳ master/config/Interval.java
- ⏳ master/config/VertxCronConfig.java
- ⏳ master/config/VertxTimerConfig.java

#### Constants
- ⏳ master/constant/Constants.java

#### Exceptions
- ⏳ master/exception/DefinedException.java
- ⏳ master/exception/ErrorEntity.java

#### Models
- ⏳ master/model/FilterProps.java

#### Request/Response Objects
- ⏳ master/io/request/BehaviourTagCreateRequest.java
- ⏳ master/io/request/BehaviourTagPutRequest.java
- ⏳ master/io/request/CTARequest.java
- ⏳ master/io/request/CTAUpdateRequest.java
- ⏳ master/io/request/RuleRequest.java
- ⏳ master/io/response/BehaviourTagsResponse.java
- ⏳ master/io/response/CTAListResponse.java
- ⏳ master/io/response/StatusWiseCount.java

#### Controllers
- ⏳ master/rest/AdminController.java
- ⏳ master/rest/BehaviourTagController.java
- ⏳ master/rest/HealthCheck.java

#### Services
- ⏳ master/service/AdminService.java
- ⏳ master/service/BehaviourTagService.java

#### Service Implementations
- ⏳ master/service/admin/AdminServiceImpl.java
- ⏳ master/service/admin/AdminServiceModule.java
- ⏳ master/service/admin/CreateCTAMapper.java
- ⏳ master/service/admin/CTAUpdateValidator.java
- ⏳ master/service/admin/DraftCTAUpdateValidator.java
- ⏳ master/service/admin/PausedCTAUpdateValidator.java
- ⏳ master/service/admin/RuleComparator.java
- ⏳ master/service/admin/RuleMapper.java
- ⏳ master/service/admin/ScheduledCTA.java
- ⏳ master/service/admin/ScheduledCTARecordMapper.java
- ⏳ master/service/admin/UpdateCTAMapper.java
- ⏳ master/service/behaviourTag/BehaviourTagMapper.java
- ⏳ master/service/behaviourTag/BehaviourTagServiceImpl.java
- ⏳ master/service/behaviourTag/BehaviourTagServiceModule.java
- ⏳ master/service/behaviourTag/BehaviourTagUpdateMapper.java
- ⏳ master/service/filters/CTAFilters.java
- ⏳ master/service/scheduler/CronMonitor.java
- ⏳ master/service/scheduler/CronMonitorModule.java

#### Utilities
- ⏳ master/util/Constants.java

#### Main Application
- ⏳ master/MainApplication.java
- ⏳ master/MainModule.java

### Configuration Files
- ⏳ thunder-api/src/main/resources/thunder-default.conf (update with new settings)
- ⏳ thunder-admin/src/main/resources/admin-default.conf (update with new settings)
- ⏳ Add bulk-read-socket-timeout to config files

## Migration Notes

### Key Adaptations Made
1. **Reactive Streams**: Converted from `io.reactivex` (RxJava 2) to `io.reactivex.rxjava3` (RxJava 3)
2. **Aerospike Client**: Created custom reactive wrapper to replace internal `com.dream11.aerospike.reactivex.client.AerospikeClient`
3. **Exception Handling**: Created `ThunderException` to replace internal `com.dream11.rest.exception.RestException`
4. **Dependency Injection**: Using `com.google.inject.Inject` (Google Guice) instead of `javax.inject.Inject`
5. **Validation**: Using `javax.validation` API for model validation

### User Cohorts Deprecation Strategy
As requested, the User Cohorts functionality will be deprecated:
- `UserCohortsClient.getUserCohorts()` → return `List.of("all")`
- Remove UserCohortsConfig
- No web-client dependency needed for this feature

## Estimated Remaining Work
- **High Priority**: CTA Repository Implementation (~362 lines, most complex)
- **Medium Priority**: BehaviourTag Repository Implementation (~177 lines)
- **Medium Priority**: Thunder API Controllers & Services
- **Medium Priority**: Thunder Admin Controllers & Services
- **Low Priority**: Configuration updates and testing

## Next Steps
1. Complete CTARepositoryImpl (largest and most complex)
2. Complete BehaviourTagRepositoryImpl
3. Copy and adapt API controllers (HealthCheck, SdkApiController, AppDebugController)
4. Copy and adapt Admin controllers (AdminController, BehaviourTagController, HealthCheck)
5. Implement service layer for both modules
6. Update configuration files
7. Test end-to-end functionality
8. Deploy and verify on Docker

## Testing Strategy
Once all implementations are in place:
1. Build the project: `mvn clean install`
2. Deploy to Docker: `./scripts/start.sh`
3. Test API endpoints:
   - Thunder API Health: `http://localhost:8080/healthcheck`
   - Thunder Admin Health: `http://localhost:8081/healthcheck`
4. Verify Aerospike connectivity
5. Test CRUD operations for each entity type

