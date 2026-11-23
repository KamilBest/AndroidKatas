package katas.ex06

import java.io.IOException

// GIVEN: callback-based API (like FusedLocationProviderClient)
data class Location(val lat: Double, val lng: Double)
interface LocationCallback { fun onLocation(location: Location); fun onError(error: Throwable) }
fun interface RequestHandle { fun cancel() }
interface LocationClient { fun requestLocation(callback: LocationCallback): RequestHandle }

suspend fun LocationClient.awaitLocation(): Location = TODO()

class BatchProcessor(private val onItem: (Int) -> Unit) {
    /** Processes items (CPU), returns how many were processed. Must react to cancellation. */
    suspend fun process(items: List<Int>): Int {
        var processed = 0
        for (item in items) {
            onItem(item)
            processed++
        }
        return processed
    }
}

interface UploadApi {
    suspend fun acquireLock()
    suspend fun upload(file: String)
    suspend fun releaseLock()
}

class Uploader(private val api: UploadApi) {
    suspend fun upload(file: String) {
        api.acquireLock()
        api.upload(file)
        api.releaseLock() // TODO: always, also on cancellation
    }
}

interface StatusApi { suspend fun isDone(): Boolean }

class StatusPoller(private val api: StatusApi, private val intervalMs: Long) {
    /*  Broken version:
        while (true) {
            try { if (api.isDone()) return; delay(intervalMs) }
            catch (e: Exception) { println("retrying") }
        }
     */
    suspend fun pollUntilDone(): Unit = TODO()
}

@Suppress("unused") private val keepImport = IOException::class
