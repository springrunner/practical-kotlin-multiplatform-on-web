// file:src/jsTest/kotlin/external/IsSortedTest.kt
package external

import external.issorted.sorted
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author springrunner.kr@gmail.com
 */
class IsSortedTest {

    @Test
    fun thingsShouldWork() {
        assertTrue(sorted(arrayOf(1, 2, 3)))
    }

    @Test
    fun thingsShouldBreak() {
        assertFalse(sorted(arrayOf(3, 1, 2)))
    }
}
