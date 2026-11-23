package katas.ex06

import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.Job

class FakeLocationClient : LocationClient {
    var callback: LocationCallback? = null
    var cancelled = false
    override fun requestLocation(callback: LocationCallback): RequestHandle {
        this.callback = callback
        return RequestHandle { cancelled = true }
    }
}

class CancellationTest {

    @Test fun `awaitLocation resumes with value`() = runTest {
        val client = FakeLocationClient()
        val result = async { client.awaitLocation() }
        runCurrent()
        client.callback!!.onLocation(Location(52.2, 21.0))
        assertEquals(Location(52.2, 21.0), result.await())
    }

    @Test fun `awaitLocation propagates error`() = runTest {
        val client = FakeLocationClient()
        val result = async { runCatching { client.awaitLocation() } }
        runCurrent()
        client.callback!!.onError(IOException("gps off"))
        assertTrue(result.await().exceptionOrNull() is IOException)
    }

    @Test fun `awaitLocation unregisters on cancel`() = runTest {
        val client = FakeLocationClient()
        val job = launch { client.awaitLocation() }
        runCurrent()
        job.cancelAndJoin()
        assertTrue(client.cancelled, "handle.cancel() not called")
        client.callback!!.onLocation(Location(0.0, 0.0)) // a late callback must not crash the app
    }

    @Test fun `cpu loop stops after cancellation`() = runTest {
        var job: Job? = null
        var seen = 0
        var finished = false
        val processor = BatchProcessor { seen++; if (seen == 3) job!!.cancel() }
        job = launch { processor.process((1..10).toList()); finished = true }
        job.join()
        assertEquals(3, seen, "loop kept running after cancel()")
        assertEquals(false, finished, "process() should end with CancellationException")
    }

    @Test fun `lock is released even when cancelled`() = runTest {
        val log = mutableListOf<String>()
        val api = object : UploadApi {
            override suspend fun acquireLock() { log += "lock" }
            override suspend fun upload(file: String) { delay(10_000); log += "uploaded" }
            override suspend fun releaseLock() { delay(100); log += "release" }
        }
        val job = launch { Uploader(api).upload("scan.pdf") }
        advanceTimeBy(1_000)
        job.cancelAndJoin()
        assertEquals(listOf("lock", "release"), log)
    }

    @Test fun `poller survives IOException but stops on cancel`() = runTest {
        var calls = 0
        val api = object : StatusApi {
            override suspend fun isDone(): Boolean { calls++; throw IOException("offline") }
        }
        val job = launch { StatusPoller(api, intervalMs = 1_000).pollUntilDone() }
        advanceTimeBy(3_500)
        assertEquals(4, calls)
        job.cancelAndJoin()
        assertTrue(job.isCancelled)
        advanceTimeBy(10_000)
        assertEquals(4, calls, "polling continued after cancel")
    }

    @Test fun `poller returns when done`() = runTest {
        var calls = 0
        val api = object : StatusApi { override suspend fun isDone() = ++calls == 3 }
        StatusPoller(api, 500).pollUntilDone()
        assertEquals(1_000, currentTime)
    }
}
