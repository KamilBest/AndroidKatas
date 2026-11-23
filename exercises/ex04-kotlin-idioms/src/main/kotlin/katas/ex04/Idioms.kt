package katas.ex04

// 1. value class: an amount in grosze. +, comparisons and equality must work.
class Grosze(val value: Long) {
    operator fun plus(other: Grosze): Grosze = TODO()
    operator fun compareTo(other: Grosze): Int = TODO()
    /** 1349 → "13,49 zł" */
    override fun toString(): String = TODO()
}

// 2. implement with delegation: class CountingSet<T>(...) : MutableSet<T> by ...
class CountingSet<T> {
    var addAttempts: Int = 0
        private set
}

// 3. add variance modifiers
class Box<T>(val value: T)
class Sink<T> {
    val received = mutableListOf<Any?>()
    fun put(item: T) { received += item }
}

// 4.
fun List<*>.firstOfType(): Nothing = TODO("make this an inline fun <reified T>")

// 5. a regular class, not a data class
class PatientKey(val pesel: String)

// 6.
class Report(private val factory: () -> String) {
    val summary: String get() = TODO("lazy")
}
