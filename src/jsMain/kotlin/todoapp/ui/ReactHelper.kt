package todoapp.ui

/**
 * 주어진 지네릭 타입으로 인스턴스를 생성 및 초기화
 */
fun <T> createInstance(block: T.() -> Unit) = Any().unsafeCast<T>().apply(block)
