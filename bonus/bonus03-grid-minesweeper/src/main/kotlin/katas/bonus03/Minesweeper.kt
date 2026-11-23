package katas.bonus03

data class Pos(val x: Int, val y: Int)

enum class Status { PLAYING, WON, LOST }

data class Game(
    val width: Int,
    val height: Int,
    val mines: Set<Pos>,
    val revealed: Set<Pos> = emptySet(),
    val flagged: Set<Pos> = emptySet(),
    val status: Status = Status.PLAYING,
) {
    fun adjacentMines(pos: Pos): Int = TODO()
    fun reveal(pos: Pos): Game = TODO()
    fun toggleFlag(pos: Pos): Game = TODO()
    /** Rows separated by '\n', no '\n' at the end. */
    fun render(): String = TODO()
}
