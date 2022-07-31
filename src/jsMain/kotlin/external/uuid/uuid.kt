@file:JsModule("uuid")
@file:JsNonModule

package external.uuid

/**
 * https://www.npmjs.com/package/uuid#uuidv4options-buffer-offset
 */
external fun v4(): String

/**
 * https://www.npmjs.com/package/uuid#uuidversionstr
 */
external fun version(uuid: String): Int
