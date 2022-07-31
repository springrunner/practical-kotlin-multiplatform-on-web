package todoapp.support

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asPromise
import kotlin.js.Promise

/**
 * 비동기 연산 결과를 다루는 Deferred를 Promise로 변환하고 오류 처리 핸들러를 연결하는 확장 함수
 */
fun <R> Deferred<Any>.onError(handler: (Throwable) -> R): Promise<R> = this.asPromise().catch(handler)
