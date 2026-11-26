package katas.ex02

import java.time.LocalDate

object ClaimsReport {

    /** A claim alone is not a duplicate: a suspicious group needs at least this many claims. */
    private const val MIN_DUPLICATE_GROUP_SIZE = 2

    /** Sum of amounts per category. Categories without claims are not in the map. */
    fun totalByCategory(claims: List<Claim>): Map<Category, Long> =
        claims
            .groupingBy { it.category }
            .fold(0L) { sum, claim -> sum + claim.amountMinor }

    /** [n] providers with the highest total, descending; a tie → alphabetically by name. */
    fun topProviders(claims: List<Claim>, n: Int): List<Pair<String, Long>> {
        require(n >= 0) { "n must not be negative, was $n" }
        return claims
            .groupBy { it.provider }
            .mapValues { (_, list) -> list.sumOf { it.amountMinor } }
            .toList()
            .sortedWith(compareByDescending<Pair<String, Long>> { it.second }.thenBy { it.first })
            .take(n)
    }

    /**
     * Groups (≥ 2) of claims with the same patient, provider, amount and date.
     * Order of groups and order inside a group: by first appearance in the list.
     */
    fun suspectedDuplicates(claims: List<Claim>): List<List<Claim>> =
        claims
            .groupBy { DuplicateKey(it.patientId, it.provider, it.amountMinor, it.date) }
            .values
            .filter { it.size >= MIN_DUPLICATE_GROUP_SIZE }

    /** The first [n] claims with amount ≥ [thresholdMinor]. The source can be infinite, so process it lazily. */
    fun firstLargeClaims(claims: Sequence<Claim>, thresholdMinor: Long, n: Int): List<Claim> {
        require(n >= 0) { "n must not be negative, was $n" }
        return claims
            .filter { it.amountMinor >= thresholdMinor }
            .take(n)
            .toList()
    }
}

/** Grouping key for [ClaimsReport.suspectedDuplicates]: claims with an equal key are suspected duplicates. */
private data class DuplicateKey(
    val patientId: String,
    val provider: String,
    val amountMinor: Long,
    val date: LocalDate,
)
