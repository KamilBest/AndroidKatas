package katas.ex02

object ClaimsReport {

    /** Sum of amounts per category. Categories without claims are not in the map. */
    fun totalByCategory(claims: List<Claim>): Map<Category, Long> =
        claims
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amountMinor } }

    /** [n] providers with the highest total, descending; a tie → alphabetically by name. */
    fun topProviders(claims: List<Claim>, n: Int): List<Pair<String, Long>> =
        claims
            .groupBy { it.provider }
            .mapValues { (_, list) -> list.sumOf { it.amountMinor } }
            .toList()
            .sortedWith(compareByDescending<Pair<String, Long>> { it.second }.thenBy { it.first })
            .take(n)

    /**
     * Groups (≥ 2) of claims with the same patient, provider, amount and date.
     * Order of groups and order inside a group: by first appearance in the list.
     */
    fun suspectedDuplicates(claims: List<Claim>): List<List<Claim>> =
        claims
            .groupBy { GroupKey(it.patientId, it.provider, it.amountMinor, it.date) }
            .values
            .filter { it.size >= 2 }

    /** The first [n] claims with amount ≥ [thresholdMinor]. The source can be infinite, so process it lazily. */
    fun firstLargeClaims(claims: Sequence<Claim>, thresholdMinor: Long, n: Int): List<Claim> =
        claims
            .filter { it.amountMinor >= thresholdMinor }
            .take(n)
            .toList()
}
