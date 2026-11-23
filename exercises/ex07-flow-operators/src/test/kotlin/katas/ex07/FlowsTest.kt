package katas.ex07

import app.cash.turbine.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeDrugApi : DrugApi {
    val calls = mutableListOf<String>()
    override suspend fun search(query: String): List<String> {
        calls += query
        delay(1_000)
        if (query == "boom") throw IOException("server error")
        return listOf("$query 200mg", "$query 400mg")
    }
}

class DrugSearchTest {
    // Note: MutableStateFlow, not MutableSharedFlow. A SharedFlow without subscribers DROPS emissions,
    // and the `stateIn` upstream starts only after a dispatch. A classic trap in tests (and in a text field).
    private val queries = MutableStateFlow("")
    private val api = FakeDrugApi()

    @Test fun `debounces typing and searches once`() = runTest {
        DrugSearch(queries, api, backgroundScope).state.test {
            assertEquals(SearchState.Idle, awaitItem())
            queries.value = "i"; advanceTimeBy(100)
            queries.value = "ib"; advanceTimeBy(100)
            queries.value = "ibu "
            assertEquals(SearchState.Loading, awaitItem())
            assertEquals(SearchState.Results(listOf("ibu 200mg", "ibu 400mg")), awaitItem())
            assertEquals(listOf("ibu"), api.calls)
        }
    }

    @Test fun `short query goes idle without calling api`() = runTest {
        DrugSearch(queries, api, backgroundScope).state.test {
            assertEquals(SearchState.Idle, awaitItem())
            queries.value = "a"; advanceTimeBy(1_000)
            expectNoEvents()
            assertTrue(api.calls.isEmpty())
        }
    }

    @Test fun `new query cancels previous request`() = runTest {
        DrugSearch(queries, api, backgroundScope).state.test {
            assertEquals(SearchState.Idle, awaitItem())
            queries.value = "apap"; advanceTimeBy(301)
            assertEquals(SearchState.Loading, awaitItem())
            queries.value = "ibuprom" // during the "apap" request (Loading == Loading, so StateFlow does not emit again)
            assertEquals(SearchState.Results(listOf("ibuprom 200mg", "ibuprom 400mg")), awaitItem())
            assertEquals(listOf("apap", "ibuprom"), api.calls)
        }
    }

    @Test fun `error does not kill the search`() = runTest {
        DrugSearch(queries, api, backgroundScope).state.test {
            assertEquals(SearchState.Idle, awaitItem())
            queries.value = "boom"
            assertEquals(SearchState.Loading, awaitItem())
            assertEquals(SearchState.Error("server error"), awaitItem())
            queries.value = "apap"
            assertEquals(SearchState.Loading, awaitItem())
            assertEquals(SearchState.Results(listOf("apap 200mg", "apap 400mg")), awaitItem())
        }
    }

    @Test fun `same query twice is ignored`() = runTest {
        DrugSearch(queries, api, backgroundScope).state.test {
            skipItems(1)
            queries.value = "apap"; skipItems(2)
            queries.value = "apap "; advanceTimeBy(2_000)
            expectNoEvents()
            assertEquals(1, api.calls.size)
        }
    }
}

class SensorFlowTest {

    @Test fun `callbackFlow registers and unregisters`() = runTest {
        val sensor = object : HeartRateSensor {
            val listeners = mutableListOf<HeartRateListener>()
            override fun register(listener: HeartRateListener) { listeners += listener }
            override fun unregister(listener: HeartRateListener) { listeners -= listener }
        }
        sensor.readings().test {
            assertEquals(1, sensor.listeners.size)
            sensor.listeners.single().onBpm(72)
            sensor.listeners.single().onBpm(75)
            assertEquals(72, awaitItem())
            assertEquals(75, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertTrue(sensor.listeners.isEmpty(), "listener leaked")
    }
}
