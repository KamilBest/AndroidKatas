package katas.ex02

import java.time.LocalDate

/**
 * The fields that make two claims look like duplicates of each other.
 *
 * A `data class` gets `equals` and `hashCode` from all four fields,
 * so it works as a composite key in `groupBy`.
 */
data class GroupKey(
    val patientId: String,
    val provider: String,
    val amountMinor: Long,
    val date: LocalDate,
)
