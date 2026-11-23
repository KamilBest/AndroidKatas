package katas.ex03

/**
 * A cache limited to [capacity] entries. `get` and `put` must run in O(1).
 * When there is no space, `put` removes the least recently used key.
 */
class LruCache<K, V>(private val capacity: Int) {
    init { require(capacity >= 0) { "capacity must be >= 0" } }

    val size: Int get() = TODO()
    operator fun get(key: K): V? = TODO()
    fun put(key: K, value: V): Unit = TODO()
    /** Keys from least to most recently used. */
    fun keysInOrder(): List<K> = TODO()
}
