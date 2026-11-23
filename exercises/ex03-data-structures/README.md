# ex03 · LRU cache in O(1)

**Difficulty:** medium · **Time:** 30 min · **Category:** Algorithms · data structures · **Type:** implement

## Goal
Write an LRU (least recently used) cache where `get` and `put` are O(1).

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
- `LinkedHashMap(capacity, 0.75f, accessOrder = true)` already does this; the task asks you to build the structure yourself.
- Sentinel `head` and `tail` nodes remove most edge-case `if`s.
