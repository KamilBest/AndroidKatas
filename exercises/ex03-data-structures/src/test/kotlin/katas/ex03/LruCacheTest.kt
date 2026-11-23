package katas.ex03

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LruCacheTest {
    @Test fun `evicts least recently used`() {
        val cache = LruCache<String, Int>(2)
        cache.put("a", 1); cache.put("b", 2)
        assertEquals(1, cache["a"])          // a becomes the most recent
        cache.put("c", 3)                    // b is evicted
        assertNull(cache["b"])
        assertEquals(listOf("a", "c"), cache.keysInOrder())
        assertEquals(2, cache.size)
    }

    @Test fun `put on existing key updates and refreshes`() {
        val cache = LruCache<String, Int>(2)
        cache.put("a", 1); cache.put("b", 2); cache.put("a", 10); cache.put("c", 3)
        assertEquals(10, cache["a"]); assertNull(cache["b"])
    }

    @Test fun `zero capacity stores nothing`() {
        val cache = LruCache<Int, Int>(0)
        cache.put(1, 1)
        assertNull(cache[1]); assertEquals(0, cache.size)
    }

    @Test fun `handles many operations quickly`() {
        val cache = LruCache<Int, Int>(1000)
        repeat(200_000) { cache.put(it, it); cache[it / 2] }
        assertEquals(1000, cache.size)
    }
}
