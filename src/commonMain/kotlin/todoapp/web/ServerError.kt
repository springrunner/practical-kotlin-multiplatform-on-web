package todoapp.web

import kotlinx.serialization.Serializable

/**
 * 서버 오류 발생시 응답 모델
 *
 * @author springrunner.kr@gmail.com
 */
@Serializable
data class ServerError(
    val path: String,
    val error: String = "Internal Server Error",
    val message: String = "An unknown server error occurred...",
    val status: Int = 500,
    val timestamp: String
)
