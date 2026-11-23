package katas.ex05

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeApi : InsuranceApi {
    var failClaims = false
    val failingPolicies = mutableSetOf<String>()
    var policyDelay = 100L
    val completed = mutableListOf<String>()

    override suspend fun profile(): Profile { delay(300); completed += "profile"; return Profile("Ania") }
    override suspend fun policyIds(): List<String> { delay(500); completed += "policies"; return listOf("P1") }
    override suspend fun claims(): List<String> {
        delay(200)
        if (failClaims) throw IOException("claims down")
        completed += "claims"; return listOf("C1")
    }
    override suspend fun policy(id: String): Policy {
        delay(policyDelay)
        if (id in failingPolicies) throw IOException("policy $id down")
        completed += id
        return Policy(id, "Health+")
    }
}

class LoadingTest {

    @Test fun `dashboard loads in parallel`() = runTest {
        val dashboard = Loader(FakeApi()).loadDashboard()
        assertEquals(Dashboard(Profile("Ania"), listOf("P1"), listOf("C1")), dashboard)
        assertEquals(500, currentTime, "should take max(300, 500, 200), not the sum")
    }

    @Test fun `dashboard fails fast and cancels siblings`() = runTest {
        val api = FakeApi().apply { failClaims = true }
        assertFailsWith<IOException> { Loader(api).loadDashboard() }
        assertEquals(200, currentTime)
        assertTrue(api.completed.isEmpty(), "siblings should be cancelled, but completed: ${api.completed}")
    }

    @Test fun `one failing policy does not cancel others`() = runTest {
        val api = FakeApi().apply { failingPolicies += "B" }
        val result = Loader(api).loadPolicies(listOf("A", "B", "C"))
        assertEquals(Policy("A", "Health+"), result.getValue("A").getOrNull())
        assertIs<IOException>(result.getValue("B").exceptionOrNull())
        assertEquals(Policy("C", "Health+"), result.getValue("C").getOrNull())
        assertEquals(100, currentTime)
    }

    @Test fun `timeout falls back to cache`() = runTest {
        val cached = Policy("A", "cached")
        val api = FakeApi().apply { policyDelay = 5_000 }
        assertEquals(cached, Loader(api, mapOf("A" to cached)).policyOrCached("A", timeoutMs = 1_000))
        assertEquals(1_000, currentTime)
        assertNull(Loader(api).policyOrCached("X", timeoutMs = 1_000))
        api.policyDelay = 10
        assertEquals("Health+", Loader(api, mapOf("A" to cached)).policyOrCached("A", 1_000)?.product)
    }

}
