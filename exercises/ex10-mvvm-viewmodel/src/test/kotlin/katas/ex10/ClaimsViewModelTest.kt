package katas.ex10

import app.cash.turbine.test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeClaimsRepository : ClaimsRepository {
    val claims = MutableStateFlow<List<Claim>>(emptyList())
    var nextRefresh: CompletableDeferred<Unit>? = null
    var failRefresh = false
    var refreshCalls = 0
    override fun observeClaims() = claims
    override suspend fun refresh() {
        refreshCalls++
        nextRefresh?.await()
        if (failRefresh) throw IOException("offline")
    }
}

class ClaimsViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    private val repo = FakeClaimsRepository()
    private val open = Claim("1", "Visit", 150_00, ClaimStatus.OPEN)
    private val paid = Claim("2", "Drugs", 1234_50, ClaimStatus.PAID)

    @Test fun `loads and maps claims`() = runTest {
        val gate = CompletableDeferred<Unit>().also { repo.nextRefresh = it }
        val vm = ClaimsViewModel(repo)
        runCurrent()
        assertTrue(vm.state.value.isLoading)
        repo.claims.value = listOf(open, paid)
        gate.complete(Unit)
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.isLoading)
        assertEquals(listOf(ClaimUi("1", "Visit", "150,00 zł", false), ClaimUi("2", "Drugs", "1234,50 zł", true)), state.claims)
        assertEquals(1, repo.refreshCalls)
    }

    @Test fun `error with empty list shows full screen error, retry clears it`() = runTest {
        repo.failRefresh = true
        val vm = ClaimsViewModel(repo)
        advanceUntilIdle()
        assertEquals("No connection", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)

        repo.failRefresh = false
        repo.claims.value = listOf(open)
        vm.onRetry()
        advanceUntilIdle()
        assertNull(vm.state.value.error)
        assertEquals(2, repo.refreshCalls)
    }

    @Test fun `error with cached data shows snackbar and keeps data`() = runTest {
        repo.claims.value = listOf(open)
        val vm = ClaimsViewModel(repo)
        advanceUntilIdle()
        repo.failRefresh = true
        vm.events.test {
            vm.onRefresh()
            assertEquals(ClaimsEvent.ShowSnackbar("Could not refresh"), awaitItem())
        }
        assertNull(vm.state.value.error)
        assertEquals(1, vm.state.value.claims.size)
        assertFalse(vm.state.value.isRefreshing)
    }

    @Test fun `refresh sets refreshing flag while running`() = runTest {
        repo.claims.value = listOf(open)
        val vm = ClaimsViewModel(repo)
        advanceUntilIdle()
        val gate = CompletableDeferred<Unit>().also { repo.nextRefresh = it }
        vm.onRefresh()
        runCurrent()
        assertTrue(vm.state.value.isRefreshing)
        assertFalse(vm.state.value.isLoading)
        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(vm.state.value.isRefreshing)
    }

    @Test fun `click emits navigation event exactly once even without collector`() = runTest {
        val vm = ClaimsViewModel(repo)
        vm.onClaimClicked("7")                      // nobody collects yet (e.g. during rotation)
        assertEquals(ClaimsEvent.OpenDetails("7"), vm.events.first())
        vm.events.test { expectNoEvents() }          // no replay
    }
}
