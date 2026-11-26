# bonus03 · Grid game: Minesweeper + BFS

**Difficulty:** medium · **Time:** 60–90 min · **Category:** Algorithms · grids and BFS · **Type:** implement

## Goal
Write the logic of Minesweeper and a shortest path in a maze, with no graphics and an immutable game state.

Two classic grid problems.
- **Minesweeper:** a board with hidden mines. Revealing a cell shows a mine (you lose) or the number of mines around it; revealing a 0 also opens the whole empty area around it. You win when every cell without a mine is revealed.
- **Shortest path:** in a maze with walls (`#`), find the shortest route between two cells, moving only up, down, left and right.

The game state is immutable: every move returns a new `Game`.

- **Files:** `Minesweeper.kt`, `Pathfinding.kt`
- **Run:** `./gradlew :bonus03-grid-minesweeper:test`

## Task
**A. Minesweeper** (`Game` is immutable, every action returns a new `Game`):
1. `adjacentMines(pos)`: the number of mines among the 8 neighbours.
2. `reveal(pos)`: a mine → `LOST` (reveal all mines); a number > 0 → reveal only this cell; 0 → reveal the whole area of zeros with its border; a flag or a finished game → no change.
3. `toggleFlag(pos)`: only on a hidden cell.
4. `WON` when all cells without mines are revealed.
5. `render()`: `#` hidden, `F` flag, `*` mine (after losing), `.` zero, a digit.

**B. Path:** `shortestPath(grid, from, to)` on a grid (`#` = wall, moves in 4 directions) → the list of cells from `from` to `to` inclusive, or `null`.

**Extra (optional):** one generic `bfs(start, neighbours)` function used by both Minesweeper and the path search.

## Background
- BFS (breadth-first search) visits cells in order of distance, using a queue (`ArrayDeque`) and a `visited` set. The first time it reaches the target is the shortest path.
- To rebuild the path, remember `cameFrom[neighbour] = current` and walk back from the target.
- Flood fill in Minesweeper is BFS that spreads only from cells with 0 neighbouring mines.
- A queue instead of recursion avoids `StackOverflowError` on big boards. `Set`/`Map` of positions instead of 2D arrays keeps the state easy to copy.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
data class Pos(val row: Int, val col: Int)

val directions = listOf(Pos(-1, 0), Pos(1, 0), Pos(0, -1), Pos(0, 1))
val around = directions.map { Pos(p.row + it.row, p.col + it.col) }

val grid = listOf("..#", "...")
grid.getOrNull(5)?.getOrNull(0)                      // null instead of an exception

val queue = ArrayDeque<Pos>()
queue.addLast(start)
val visited = mutableSetOf(start)
val cameFrom = mutableMapOf<Pos, Pos>()
while (queue.isNotEmpty()) {
    val current = queue.removeFirst()
    // for every new neighbour: visited += it; cameFrom[it] = current; queue.addLast(it)
}

generateSequence(target) { cameFrom[it] }.toList().reversed()   // walk back to the start

buildString {
    append('#')
    appendLine()
}
listOf(".", "1").joinToString("")                    // ".1"
```

- [API: ArrayDeque](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-array-deque/)
- [Kotlin: collections overview](https://kotlinlang.org/docs/collections-overview.html)
