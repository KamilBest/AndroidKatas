# ex03 · LRU cache in O(1)

**Difficulty:** medium · **Time:** 30 min · **Category:** Algorithms · data structures · **Type:** implement

## Goal
Write an LRU (least recently used) cache where `get` and `put` are O(1).

A cache keeps a limited number of values in memory so the app does not load them again (for example decoded images or API responses). When the cache is full and a new value comes in, one old value must go. An LRU (least recently used) cache removes the one that was read or written the longest time ago.

Example with capacity 2: `put(a)`, `put(b)`, `get(a)`, `put(c)`. Now `b` is removed, because `a` was used more recently. `keysInOrder()` returns `[a, c]`.

`get` and `put` must take the same short time no matter how many entries there are (the test runs 200,000 operations).

- **File:** `LruCache.kt`
- **Run:** `./gradlew :ex03-data-structures:test`

## Task
A cache with a given `capacity`:
- `get(key)` returns the value and marks the key as just used,
- `put(key, value)` adds or overwrites; when there is no room, it removes the least recently used key,
- `keysInOrder()` returns keys from the oldest to the newest,
- capacity 0 stores nothing.

Before you code, write your approach and its complexity in a comment.

**Extra (optional):** make a thread-safe version and explain why `ConcurrentHashMap` alone is not enough.

## Background
- O(1) needs two structures: a hash map finds the node, a doubly linked list keeps the usage order.
- `LinkedHashMap(capacity, 0.75f, true)` (the last argument is `accessOrder`) already does this; the task asks you to build the structure yourself.
- Sentinel `head` and `tail` nodes remove most edge-case `if`s.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
class Node(val key: String, var value: Int) {
    var prev: Node? = null
    var next: Node? = null
}

val index = HashMap<String, Node>()
index["a"] = Node("a", 1)     // O(1)
val node = index["a"]         // O(1), null when missing
index.remove("a")

// Unlink a node from a doubly linked list
node?.prev?.next = node?.next
node?.next?.prev = node?.prev

// Only to compare with your version (the last argument is accessOrder):
val builtIn = LinkedHashMap<String, Int>(16, 0.75f, true)
```

- [Kotlin: collections overview (Map)](https://kotlinlang.org/docs/collections-overview.html#map)
- [Java: LinkedHashMap (access order)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/LinkedHashMap.html)
