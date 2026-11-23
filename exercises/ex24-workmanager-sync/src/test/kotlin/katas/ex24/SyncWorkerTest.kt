package katas.ex24

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

// GIVEN: do not change
class FakeOutbox(var report: SyncReport = SyncReport(sent = 0, hasRetryable = false)) : Outbox {
    var calls = 0
    override suspend fun sendPending(): SyncReport { calls++; return report }
}

@RunWith(AndroidJUnit4::class)
class SyncWorkerTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val outbox = FakeOutbox()

    private fun worker(runAttempt: Int = 0) =
        TestListenableWorkerBuilder<SyncWorker>(context)
            .setWorkerFactory(SyncWorkerFactory(outbox))
            .setRunAttemptCount(runAttempt)
            .build()

    @Test fun `success reports how many were sent`() = runTest {
        outbox.report = SyncReport(sent = 3, hasRetryable = false)
        val result = worker().doWork()
        assertEquals(ListenableWorker.Result.success(workDataOf(SyncWorker.KEY_SENT to 3)), result)
        assertEquals(1, outbox.calls)
    }

    @Test fun `retryable problems ask WorkManager to retry`() = runTest {
        outbox.report = SyncReport(sent = 1, hasRetryable = true)
        assertEquals(ListenableWorker.Result.retry(), worker(runAttempt = 0).doWork())
        assertEquals(ListenableWorker.Result.retry(), worker(runAttempt = SyncWorker.MAX_ATTEMPTS - 2).doWork())
    }

    @Test fun `gives up after max attempts`() = runTest {
        outbox.report = SyncReport(sent = 0, hasRetryable = true)
        assertIs<ListenableWorker.Result.Failure>(worker(runAttempt = SyncWorker.MAX_ATTEMPTS - 1).doWork())
    }
}

@RunWith(AndroidJUnit4::class)
class SyncSchedulerTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var workManager: WorkManager

    @Before fun setUp() {
        val config = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .setWorkerFactory(SyncWorkerFactory(FakeOutbox()))
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @Test fun `request needs network and uses exponential backoff`() {
        val spec = SyncScheduler.syncRequest().workSpec
        assertEquals(NetworkType.CONNECTED, spec.constraints.requiredNetworkType)
        assertEquals(BackoffPolicy.EXPONENTIAL, spec.backoffPolicy)
        assertEquals(TimeUnit.SECONDS.toMillis(30), spec.backoffDelayDuration)
    }

    @Test fun `scheduling twice keeps one unique work`() {
        SyncScheduler.schedule(workManager)
        val firstId = workManager.getWorkInfosForUniqueWork(SyncWorker.UNIQUE_NAME).get().single().id
        SyncScheduler.schedule(workManager)
        val infos = workManager.getWorkInfosForUniqueWork(SyncWorker.UNIQUE_NAME).get()
        assertEquals(1, infos.size)
        assertEquals(firstId, infos.single().id, "KEEP: the already waiting work stays")
        assertEquals(WorkInfo.State.ENQUEUED, infos.single().state) // no network in the test → it waits
    }
}
