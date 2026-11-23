package katas.ex24

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val outbox: Outbox,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = TODO()

    companion object {
        const val UNIQUE_NAME = "outbox-sync"
        const val KEY_SENT = "sent"
        const val MAX_ATTEMPTS = 5
    }
}

/** WorkManager creates workers itself, so you inject dependencies through your own factory. */
class SyncWorkerFactory(private val outbox: Outbox) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? = TODO()
}

object SyncScheduler {
    /** A one-time job: only with network, exponential backoff from 30 s. */
    fun syncRequest(): OneTimeWorkRequest = TODO()

    /** Enqueues the sync as unique work. Further calls while it is waiting change nothing. */
    fun schedule(workManager: WorkManager): Unit = TODO()
}
