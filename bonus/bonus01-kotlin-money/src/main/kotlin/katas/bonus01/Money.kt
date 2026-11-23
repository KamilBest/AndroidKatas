package katas.bonus01

import java.math.BigDecimal

/**
 * An amount in PLN stored in grosze.
 * Remove `TODO()` and implement. You may change the signatures only if you update the tests.
 */
@JvmInline
value class Money private constructor(val minor: Long) : Comparable<Money> {

    operator fun plus(other: Money): Money = Money(this.minor.plus(other.minor))
    operator fun minus(other: Money): Money = Money(this.minor.minus(other.minor))
    operator fun times(factor: Int): Money =Money(this.minor.times(factor))
    operator fun unaryMinus(): Money = Money(this.minor.times(-1))
    override fun compareTo(other: Money): Int = this.minor.compareTo(other.minor)

    /** A percent of the amount, e.g. `percent(BigDecimal("3"))` = 3%. Rounded HALF_EVEN to a grosz. */
    fun percent(percent: BigDecimal): Money = TODO()

    /** Split into [parts] instalments; the leftover grosze go to the first instalments. */
    fun split(parts: Int): List<Money> = TODO()

    /** Split proportionally to [ratios]; the sum of the result == this. */
    fun allocate(ratios: List<Int>): List<Money> = TODO()

    override fun toString(): String = TODO()

    companion object {
        val ZERO: Money = Money(0)
        fun ofMinor(minor: Long): Money = Money(minor)
        fun of(amount: String): Money = Money(BigDecimal(amount).longValueExact())
    }
}
