# ex04 · Kotlin idioms: value class, delegation, generics

**Difficulty:** medium · **Time:** 45 min · **Category:** Kotlin · language features · **Type:** implement

## Goal
Implement six small Kotlin features: a value class, delegation, variance, `reified`, `equals`/`hashCode` and `lazy`. Each takes a few lines.

- **File:** `Idioms.kt`
- **Run:** `./gradlew :ex04-kotlin-idioms:test`

## Task
1. `Grosze`: a `value class` with `+`, comparisons and `toString()` → `"13,49 zł"`.
2. `CountingSet<T>`: a `MutableSet<T>` built with delegation (`by`) that counts add attempts.
3. `Box<T>` can be assigned to `Box<Any>`, and `Sink<Any>` to `Sink<String>`.
4. `inline fun <reified T> List<*>.firstOfType(): T?`
5. `PatientKey`: `equals`/`hashCode` ignore spaces and letter case.
6. `Report.summary` is computed lazily and only once.

The tests do not compile until you do points 2, 3 and 4, because they change *types*. Start with them.

**Extra (optional):** check the bytecode (*Tools → Kotlin → Show Kotlin Bytecode*) to see when `Grosze` gets boxed.

## Background
- `value class` wraps one value with no allocation, until it is used as a generic, nullable or interface type (then it is boxed).
- Class delegation (`by`) is safer than extending `HashSet`: `HashSet.addAll` calls `add`, so overriding both counts twice.
- Variance: `out T` = producer (only returns T), `in T` = consumer (only takes T). Like `List<out E>` and `Comparable<in T>`.
- `reified` works only in `inline` functions, because the compiler pastes the real type at the call site (no type erasure).
- Contract: equal objects must have equal `hashCode`.
