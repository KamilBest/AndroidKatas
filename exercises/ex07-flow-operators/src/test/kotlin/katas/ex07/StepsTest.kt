package katas.ex07

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Tests for single steps: make them green before the other tests.
class StepsTest {
    @Test fun `trims query`() = assertEquals("apap", normalizeQuery("  apap "))
    @Test fun `two chars is enough`() = assertEquals("ib", normalizeQuery("ib"))
    @Test fun `one char is too short`() = assertNull(normalizeQuery("a"))
    @Test fun `blank is too short`() = assertNull(normalizeQuery("   "))
    @Test fun `spaces do not count as chars`() = assertNull(normalizeQuery(" a "))
}
