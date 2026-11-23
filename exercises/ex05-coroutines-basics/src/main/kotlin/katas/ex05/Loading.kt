package katas.ex05

// GIVEN: model and API
data class Profile(val name: String)
data class Policy(val id: String, val product: String)
data class Dashboard(val profile: Profile, val policies: List<String>, val claims: List<String>)

interface InsuranceApi {
    suspend fun profile(): Profile
    suspend fun policyIds(): List<String>
    suspend fun claims(): List<String>
    suspend fun policy(id: String): Policy
}

class Loader(private val api: InsuranceApi, private val cache: Map<String, Policy> = emptyMap()) {

    suspend fun loadDashboard(): Dashboard = TODO()

    suspend fun loadPolicies(ids: List<String>): Map<String, Result<Policy>> = TODO()

    suspend fun policyOrCached(id: String, timeoutMs: Long): Policy? = TODO()
}
