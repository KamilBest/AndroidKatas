# ex25 · Compose: a stateless screen and state hoisting

**Difficulty:** medium · **Time:** 45 min · **Category:** Compose · state and UI · **Type:** implement

## Goal
Write the "Today's doses" screen as a stateless composable that passes ready UI tests. The tests run on the JVM (Robolectric), no emulator.

A screen with today's medicine doses. The user can tick a dose as taken and hide the doses that are already taken. The composable gets the whole state as a parameter and reports user actions through lambdas; it keeps no state of its own (this is called state hoisting).

The ready UI tests find elements by text and by test tag, click them and check the result.

Example: 3 doses, 1 taken: the header says `"Today: 1/3 taken"`.

- **File:** `DoseScreen.kt` (the state in `DoseUiState.kt` is ready)
- **Run:** `./gradlew :ex25-compose-state-ui:testDebugUnitTest`

## Task
`DoseListScreen(state, onToggle, onOnlyPendingChange, modifier)`, with no state of its own:
1. A header `"Today: X/Y taken"` (counted from all doses, not only the visible ones).
2. A row with a switch `"Show only not taken"`: the whole row is clickable, `testTag("only-pending")`.
3. A list (`LazyColumn`, `key = id`): row text `"Ibuprom 200 mg · 08:00"`, the whole row is `toggleable` with `testTag("dose-<id>")`.
4. An empty list after filtering → `"No doses to show"`.
5. `@Preview` of an empty and a full state.

**Extra (optional):** add a stateful `ExpandableNote(text)` with "Show more/less" that survives rotation (`rememberSaveable`), and a test with `StateRestorationTester`.

## Background
- State hoisting: the screen gets `state` and sends changes up as lambdas. It is easy to preview, test and reuse.
- `toggleable` on the whole row and `onCheckedChange = null` on the inner `Switch`/`Checkbox` give one node for TalkBack and for tests.
- `key` in `LazyColumn` lets Compose recognise the same row after the list changes.
- `modifier: Modifier = Modifier` is the first optional parameter and goes to the root element.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
@Composable
fun TodoList(
    state: TodoState,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        item { Text("Header") }
        items(state.items, key = { it.id }) { todo ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .toggleable(value = todo.done, onValueChange = { onToggle(todo.id) })
                    .testTag("todo-${todo.id}"),
            ) {
                Text("${todo.title} · ${todo.time}")
                Checkbox(checked = todo.done, onCheckedChange = null)   // the row handles clicks
            }
        }
    }
}

@Preview
@Composable
private fun TodoListPreview() {
    TodoList(TodoState(items = emptyList()), onToggle = {})
}
```

- [Android: state and state hoisting](https://developer.android.com/develop/ui/compose/state)
- [Android: lists and grids](https://developer.android.com/develop/ui/compose/lists)
- [Android: semantics in Compose](https://developer.android.com/develop/ui/compose/accessibility/semantics)
