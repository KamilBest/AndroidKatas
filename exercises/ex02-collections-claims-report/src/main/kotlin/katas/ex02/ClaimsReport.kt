package katas.ex02

object ClaimsReport {

    /** Sum of amounts per category. Categories without claims are not in the map. */
    fun totalByCategory(claims: List<Claim>): Map<Category, Long> = TODO()

    /** [n] providers with the highest total, descending; a tie → alphabetically by name. */
    fun topProviders(claims: List<Claim>, n: Int): List<Pair<String, Long>> = TODO()

    /**
     * Groups (≥ 2) of claims with the same patient, provider, amount and date.
     * Order of groups and order inside a group: by first appearance in the list.
     */
    fun suspectedDuplicates(claims: List<Claim>): List<List<Claim>> = TODO()

    /** The first [n] claims with amount ≥ [thresholdMinor]. The source can be infinite, so process it lazily. */
    fun firstLargeClaims(claims: Sequence<Claim>, thresholdMinor: Long, n: Int): List<Claim> = TODO()
}
