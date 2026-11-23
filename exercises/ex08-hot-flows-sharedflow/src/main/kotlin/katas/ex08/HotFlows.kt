package katas.ex08

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

sealed interface SessionEvent {
    data object SessionExpired : SessionEvent
    data class PolicyUpdated(val policyId: String) : SessionEvent
}

class SessionEvents {
    val events: SharedFlow<SessionEvent> = TODO()
    /** Called from callbacks (non-suspend). Returns whether the event was accepted. */
    fun publish(event: SessionEvent): Boolean = TODO()
}

class DownloadQueue {
    suspend fun enqueue(fileId: String): Unit = TODO()
    /** A stream of jobs for workers; each job goes to exactly one worker. */
    val jobs: Flow<String> = TODO()
}

class HeartRateHub(sensor: Flow<Int>, scope: CoroutineScope) {
    val readings: SharedFlow<Int> = TODO()
}
