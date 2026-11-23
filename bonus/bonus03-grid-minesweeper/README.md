# bonus03 · Grid game: Minesweeper + BFS

**Difficulty:** medium · **Time:** 60–90 min · **Category:** Algorithms · grids and BFS · **Type:** implement

## Goal
Write the logic of Minesweeper and a shortest path in a maze, with no graphics and an immutable game state.

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
