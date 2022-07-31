// file:src/jsMain/kotlin/types/issorted/is-sorted.kt
package external.issorted

@JsModule("is-sorted")
@JsNonModule
external fun <T> sorted(a: Array<T>): Boolean
