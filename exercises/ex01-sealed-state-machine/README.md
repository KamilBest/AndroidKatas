# ex01 · Claim state machine

**Difficulty:** easy · **Time:** 30 min · **Category:** Kotlin · state machine (reducer) · **Type:** implement

## Goal
Write the state machine of a refund claim as one pure function: `(state, event) -> next state`. An event that is not allowed must never change the state.

A refund claim goes through fixed steps: the patient writes it, sends it, a reviewer checks it, and it ends up paid or rejected. A state machine describes this as a list of states plus rules saying which event moves the claim from one state to the next. Events can come at the wrong time (a double click, a late message), so for every pair (state, event) the machine decides: move, or ignore and say why.

Example: in `Submitted`, `AssignReviewer("Anna")` moves the claim to `UnderReview("Anna")`. In `Submitted`, `Pay` is `Ignored("Waiting for a reviewer")` and the claim stays `Submitted`.

- **File:** `ClaimMachine.kt`
- **Run:** `./gradlew :ex01-sealed-state-machine:test`

## Task
```
Draft ─Submit→ Submitted ─AssignReviewer→ UnderReview ─Approve→ Approved ─Pay→ Paid
                                           │  ▲    └─Reject→ Rejected
                                RequestInfo│  │ProvideInfo
                                           ▼  │
                                         NeedsInfo
```
1. `ClaimMachine.reduce(state, event)` returns `Moved(newState)` for an allowed event and `Ignored(reason)` for every other event.
   - `NeedsInfo` keeps the reviewer. `ProvideInfo` goes back to `UnderReview` with the same reviewer.
   - `Approve` with an amount ≤ 0 and `Reject` with a blank reason are `Ignored`.
   - `Rejected` and `Paid` are final states: they ignore every event.
2. `ClaimState.isTerminal`: `true` only for the final states.
3. `ClaimMachine.replay(events)`: start from `Draft`, apply all events in order and return the last state. Ignored events change nothing.

Rule: the `when` over states has **no `else`** branch.

**Extra (optional):** add an `Appealed` state (an appeal after `Rejected`) only in code, see where the compiler asks for changes, then undo it.

## Background
### States and transitions
| State | Meaning | Allowed events | Example `Ignored` reason |
|---|---|---|---|
| `Draft` | the patient is still writing the claim | `Submit` → `Submitted` | "A draft can only be submitted" |
| `Submitted` | sent, waiting for a reviewer | `AssignReviewer(name)` → `UnderReview(name)` | "Waiting for a reviewer" |
| `UnderReview(reviewer)` | a reviewer checks the claim | `RequestInfo` → `NeedsInfo`, `Approve` → `Approved`, `Reject` → `Rejected` | "Amount must be greater than 0", "Rejection needs a reason", "Only Approve, Reject or RequestInfo here" |
| `NeedsInfo(reviewer, question)` | the reviewer asked the patient a question | `ProvideInfo` → `UnderReview(same reviewer)` | "Waiting for an answer" |
| `Approved(amount)` | the refund is accepted | `Pay` → `Paid` | "An approved claim can only be paid" |
| `Rejected(reason)` | final | none | "The claim is closed" |
| `Paid` | final | none | "The claim is closed" |

### The reducer pattern
A reducer is a pure function `(state, event) -> newState`: no I/O, no coroutines, no hidden fields. The same input always gives the same output, so you test it with plain values. It is the core of MVI (see ex11) and Redux. `replay` is a `fold` over events with the reducer, which is how event sourcing rebuilds state from history.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
sealed interface Light {
    data object Off : Light
    data class On(val brightness: Int) : Light
}

// No else: add a new Light and the compiler shows every `when` to update
fun describe(light: Light): String = when (light) {
    Light.Off -> "off"
    is Light.On -> "on at ${light.brightness}%"
}

listOf(1, 2, 3).fold(0) { sum, n -> sum + n }   // 6
```

- [Kotlin: sealed classes and interfaces](https://kotlinlang.org/docs/sealed-classes.html)
- [Kotlin: when expressions](https://kotlinlang.org/docs/control-flow.html#when-expressions-and-statements)
- [Kotlin: fold and reduce](https://kotlinlang.org/docs/collection-aggregate.html#fold-and-reduce)
