package todoapp.support

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * 현재 시간으로 LocalDateTime 객체를 생성하는 확장 함수
 */
fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()) =
    Clock.System.now().toLocalDateTime(timeZone)
