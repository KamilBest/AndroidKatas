package katas.ex23

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Tests for single steps: make them green before PharmacyPagingSourceTest.
class StepsTest {
    @Test fun `first page has no previous`() = assertNull(prevKeyFor(1))
    @Test fun `previous of page 3 is 2`() = assertEquals(2, prevKeyFor(3))

    @Test fun `next of middle page`() = assertEquals(3, nextKeyFor(page = 2, totalPages = 5, itemCount = 20))
    @Test fun `last page has no next`() = assertNull(nextKeyFor(page = 5, totalPages = 5, itemCount = 7))
    @Test fun `empty page ends the list even if api says more`() = assertNull(nextKeyFor(page = 2, totalPages = 5, itemCount = 0))
}
