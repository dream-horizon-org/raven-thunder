package com.raven.thunder.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.raven.thunder.core.io.Response;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.CompletionStage;
import org.junit.jupiter.api.Test;

class ResponseWrapperTest {

  @Test
  void fromMaybe_successAndEmpty() throws Exception {
    CompletionStage<Response<String>> cs1 = ResponseWrapper.fromMaybe(Maybe.just("ok"), null, 200);
    Response<String> r1 = cs1.toCompletableFuture().get();
    assertThat(r1.isSuccess()).isTrue();
    assertThat(r1.getData()).isEqualTo("ok");
    assertThat(r1.getStatusCode()).isEqualTo(200);

    CompletionStage<Response<String>> cs2 = ResponseWrapper.fromMaybe(Maybe.empty(), "def", 200);
    Response<String> r2 = cs2.toCompletableFuture().get();
    assertThat(r2.isSuccess()).isTrue();
    assertThat(r2.getData()).isEqualTo("def");
  }

  @Test
  void fromSingle_successAndError() throws Exception {
    CompletionStage<Response<String>> cs1 = ResponseWrapper.fromSingle(Single.just("s1"), 201);
    Response<String> r1 = cs1.toCompletableFuture().get();
    assertThat(r1.getData()).isEqualTo("s1");
    assertThat(r1.getStatusCode()).isEqualTo(201);

    CompletionStage<Response<String>> cs2 =
        ResponseWrapper.fromSingle(Single.error(new RuntimeException("boom")), 500);
    Response<String> r2 = cs2.toCompletableFuture().get();
    assertThat(r2.isSuccess()).isFalse();
    assertThat(r2.getError()).contains("boom");
  }

  @Test
  void fromCompletable_successAndError() throws Exception {
    CompletionStage<Response<String>> cs1 =
        ResponseWrapper.fromCompletable(Completable.complete(), "done", 200);
    Response<String> r1 = cs1.toCompletableFuture().get();
    assertThat(r1.isSuccess()).isTrue();
    assertThat(r1.getData()).isEqualTo("done");

    CompletionStage<Response<String>> cs2 =
        ResponseWrapper.fromCompletable(Completable.error(new RuntimeException("err")), null, 500);
    Response<String> r2 = cs2.toCompletableFuture().get();
    assertThat(r2.isSuccess()).isFalse();
    assertThat(r2.getError()).contains("err");
  }
}
