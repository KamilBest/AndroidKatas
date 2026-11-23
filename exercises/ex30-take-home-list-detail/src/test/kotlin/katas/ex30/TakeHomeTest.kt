package katas.ex30

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals

// GIVEN: do not change
class FakePrescriptionsRepository : PrescriptionsRepository {
    val data = MutableStateFlow<List<Prescription>>(emptyList())
    var failRefresh = false
    var gate: CompletableDeferred<Unit>? = null
    var refreshCalls = 0
    override fun observe(): Flow<List<Prescription>> = data
    override suspend fun refresh() {
        refreshCalls++
        gate?.await()
        if (failRefresh) throw IOException("offline")
    }
    override fun observeById(id: String): Flow<Prescription?> = data.map { list -> list.firstOrNull { it.id == id } }
}

// "Today" in Warsaw is 2026-03-12 (23:30 UTC 11.03 = 00:30 12.03 Polish time).
private val clock: Clock = Clock.fixed(Instant.parse("2026-03-11T23:30:00Z"), ZoneId.of("UTC"))

private fun rx(id: String, until: String, redeemed: Boolean = false) =
    Prescription(id, "Amotaks", "1000 mg", LocalDate.parse(until), redeemed)

class MappingTest {
    @Test fun `maps prescription to list item`() {
        assertEquals(PrescriptionItem("1", "Amotaks 1000 mg", "Valid until 12.03.2026", Badge.ACTIVE), rx("1", "2026-03-12").toItem(clock))
        assertEquals(Badge.EXPIRED, rx("2", "2026-03-11").toItem(clock).badge)
        assertEquals(Badge.REDEEMED, rx("3", "2026-01-01", redeemed = true).toItem(clock).badge)
    }
}

class PrescriptionListViewModelTest {
    @get:Rule val main = MainDispatcherRule()
    private val repo = FakePrescriptionsRepository()

    @Test fun `loading then content sorted active redeemed expired`() = runTest {
        val gate = CompletableDeferred<Unit>().also { repo.gate = it }
        repo.data.value = listOf(
            rx("expired", "2026-03-01"),
            rx("redeemed", "2026-05-01", redeemed = true),
            rx("activeLater", "2026-06-01"),
            rx("activeSoon", "2026-03-20"),
        )
        val vm = PrescriptionListViewModel(repo, clock)
        vm.state.test {
            assertEquals(ListUiState.Loading, awaitItem())
            gate.complete(Unit)
            val content = awaitItem() as ListUiState.Content
            assertEquals(listOf("activeSoon", "activeLater", "redeemed", "expired"), content.items.map { it.id })
            assertEquals(false, content.isRefreshing)
        }
    }

    @Test fun `empty database after refresh shows empty state`() = runTest {
        val vm = PrescriptionListViewModel(repo, clock)
        vm.state.test {
            advanceUntilIdle()
            assertEquals(ListUiState.Empty, expectMostRecentItem())
        }
    }

    @Test fun `refresh error with empty database shows error`() = runTest {
        repo.failRefresh = true
        val vm = PrescriptionListViewModel(repo, clock)
        vm.state.test {
            advanceUntilIdle()
            assertEquals(ListUiState.Error("Could not load prescriptions"), expectMostRecentItem())
        }
    }

    @Test fun `refresh error with data keeps content and sends message`() = runTest {
        repo.data.value = listOf(rx("1", "2026-04-01"))
        val vm = PrescriptionListViewModel(repo, clock)
        vm.state.test {
            advanceUntilIdle()
            expectMostRecentItem()
            repo.failRefresh = true
            vm.messages.test {
                vm.onRefresh()
                assertEquals("No connection, showing saved data", awaitItem())
            }
            assertEquals(1, (vm.state.value as ListUiState.Content).items.size)
        }
    }

    @Test fun `pull to refresh sets refreshing flag`() = runTest {
        repo.data.value = listOf(rx("1", "2026-04-01"))
        val vm = PrescriptionListViewModel(repo, clock)
        vm.state.test {
            advanceUntilIdle()
            expectMostRecentItem()
            val gate = CompletableDeferred<Unit>().also { repo.gate = it }
            vm.onRefresh()
            runCurrent()
            assertEquals(true, (expectMostRecentItem() as ListUiState.Content).isRefreshing)
            gate.complete(Unit)
            advanceUntilIdle()
            assertEquals(false, (expectMostRecentItem() as ListUiState.Content).isRefreshing)
            assertEquals(2, repo.refreshCalls)
        }
    }
}

class PrescriptionDetailViewModelTest {
    @get:Rule val main = MainDispatcherRule()
    private val repo = FakePrescriptionsRepository()

    @Test fun `reads id from navigation arguments`() = runTest {
        repo.data.value = listOf(rx("1", "2026-04-01"), rx("2", "2026-03-01"))
        val vm = PrescriptionDetailViewModel(SavedStateHandle(mapOf("id" to "2")), repo, clock)
        vm.state.test {
            advanceUntilIdle()
            assertEquals(DetailUiState.Found(rx("2", "2026-03-01").toItem(clock)), expectMostRecentItem())
        }
    }

    @Test fun `unknown id shows not found`() = runTest {
        val vm = PrescriptionDetailViewModel(SavedStateHandle(mapOf("id" to "x")), repo, clock)
        vm.state.test {
            advanceUntilIdle()
            assertEquals(DetailUiState.NotFound, expectMostRecentItem())
        }
    }
}
