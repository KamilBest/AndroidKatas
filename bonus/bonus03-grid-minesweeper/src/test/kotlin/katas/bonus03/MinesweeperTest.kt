package katas.bonus03

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class MinesweeperTest {
    // 4x3, mine at (3,0):
    // . . 1 *
    // . . 1 1
    // . . . .
    private val game = Game(width = 4, height = 3, mines = setOf(Pos(3, 0)))

    @Test fun `counts adjacent mines`() {
        assertEquals(1, game.adjacentMines(Pos(2, 0)))
        assertEquals(1, game.adjacentMines(Pos(2, 1)))
        assertEquals(0, game.adjacentMines(Pos(0, 0)))
        val dense = Game(3, 3, mines = setOf(Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(0, 1), Pos(2, 1), Pos(0, 2), Pos(1, 2), Pos(2, 2)))
        assertEquals(8, dense.adjacentMines(Pos(1, 1)))
    }

    @Test fun `reveal zero cascades and wins`() {
        val after = game.reveal(Pos(0, 2))
        assertEquals(
            """
            ..1#
            ..11
            ....
            """.trimIndent(),
            after.render(),
        )
        assertEquals(Status.WON, after.status)
    }

    @Test fun `reveal number opens only that cell`() {
        val after = game.reveal(Pos(2, 0))
        assertEquals(setOf(Pos(2, 0)), after.revealed)
        assertEquals(Status.PLAYING, after.status)
    }

    @Test fun `hitting a mine loses and shows mines`() {
        val after = game.reveal(Pos(3, 0))
        assertEquals(Status.LOST, after.status)
        assertEquals("###*\n####\n####", after.render())
        assertSame(after, after.reveal(Pos(0, 0)), "no moves after game over")
    }

    @Test fun `flags block reveal and cascade`() {
        val flagged = game.toggleFlag(Pos(3, 0)).toggleFlag(Pos(0, 1))
        assertEquals("###F\nF###\n####", flagged.render())
        assertSame(flagged, flagged.reveal(Pos(3, 0)))
        val opened = flagged.reveal(Pos(3, 2))
        assertEquals(true, Pos(0, 1) !in opened.revealed)
        assertEquals(game, game.toggleFlag(Pos(1, 1)).toggleFlag(Pos(1, 1)))
    }

    @Test fun `revealing outside the board is a no-op`() {
        assertSame(game, game.reveal(Pos(-1, 0)))
        assertSame(game, game.reveal(Pos(4, 0)))
    }

    @Test fun `large board does not overflow the stack`() {
        val big = Game(400, 400, mines = setOf(Pos(399, 399)))
        assertEquals(Status.WON, big.reveal(Pos(0, 0)).status)
    }
}

class PathfindingTest {
    private val maze = listOf(
        "S..#....",
        ".#.#.##.",
        ".#...#..",
        ".####.#.",
        "......#E",
    )

    @Test fun `finds shortest path`() {
        val path = assertNotNull(shortestPath(maze, Pos(0, 0), Pos(7, 4)))
        assertEquals(Pos(0, 0), path.first())
        assertEquals(Pos(7, 4), path.last())
        assertEquals(15, path.size - 1)
        path.zipWithNext().forEach { (a, b) -> assertEquals(1, kotlin.math.abs(a.x - b.x) + kotlin.math.abs(a.y - b.y)) }
    }

    @Test fun `no path`() {
        assertNull(shortestPath(listOf("S#E"), Pos(0, 0), Pos(2, 0)))
        assertNull(shortestPath(listOf("S.#"), Pos(0, 0), Pos(2, 0)))
    }

    @Test fun `start equals end`() {
        assertEquals(listOf(Pos(1, 0)), shortestPath(listOf("..."), Pos(1, 0), Pos(1, 0)))
    }
}
