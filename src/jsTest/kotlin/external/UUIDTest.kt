package external

import external.uuid.v4
import external.uuid.version
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UUIDTest {

    @Test
    fun thingsShouldWork() {
        assertTrue(v4().isNotBlank())
        assertEquals(4, version(v4()))
    }
}
