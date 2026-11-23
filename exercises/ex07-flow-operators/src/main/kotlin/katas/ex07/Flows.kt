package katas.ex07

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

// GIVEN
interface DrugApi { suspend fun search(query: String): List<String> }

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data class Results(val drugs: List<String>) : SearchState
    data class Error(val message: String) : SearchState
}

class DrugSearch(queries: Flow<String>, api: DrugApi, scope: CoroutineScope) {
    val state: StateFlow<SearchState> = TODO()
}

/** Step 1: the query after `trim()`; `null` when shorter than 2 characters (then state `Idle`, no API call). */
fun normalizeQuery(raw: String): String? = TODO()

// GIVEN: callback-based sensor API
fun interface HeartRateListener { fun onBpm(bpm: Int) }
interface HeartRateSensor {
    fun register(listener: HeartRateListener)
    fun unregister(listener: HeartRateListener)
}

fun HeartRateSensor.readings(): Flow<Int> = TODO()
