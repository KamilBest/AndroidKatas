package katas.ex24

// GIVEN: a queue of operations waiting to be sent (outbox). The real implementation is backed by Room.
data class SyncReport(
    /** How many operations were sent. */
    val sent: Int,
    /** Whether there are operations worth retrying (network, 5xx). */
    val hasRetryable: Boolean,
)

interface Outbox {
    /** Sends everything that is waiting. Does not throw network errors, only reports them. */
    suspend fun sendPending(): SyncReport
}
