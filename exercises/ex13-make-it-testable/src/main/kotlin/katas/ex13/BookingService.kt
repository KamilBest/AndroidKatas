package katas.ex13

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.UUID

// TODO: list the hidden dependencies here and what they break.

data class BookingRequest(val id: String, val patientId: String, val slot: LocalDateTime)

sealed interface BookingResult {
    data class Booked(val id: String, val confirmationCode: String) : BookingResult
    data object TooLate : BookingResult
    data object ClinicClosed : BookingResult
    data class Failed(val retryable: Boolean) : BookingResult
}

/** Pretends to be an HTTP library: a singleton with global state. */
object HttpClient {
    suspend fun post(path: String, body: Any) {
        delay(500)
        if (Math.random() < 0.1) throw IOException("timeout")
        println("POST $path $body")
    }
}

object AnalyticsSdk {
    suspend fun send(event: String, params: Map<String, String>) { delay(2_000); println("$event $params") }
}

/**
 * Books a visit.
 * - a slot less than 2 h from now → TooLate,
 * - Saturday/Sunday → ClinicClosed,
 * - IOException from the API → Failed(retryable = true), any other exception → Failed(retryable = false),
 * - success → event "booking_created" (fire-and-forget) and Booked(id, "WAW-" + the first 6 characters of id in upper case).
 * Time is in the clinic's time zone (Europe/Warsaw).
 */
@OptIn(DelicateCoroutinesApi::class)
object BookingService {
    suspend fun book(patientId: String, slot: LocalDateTime): BookingResult {
        if (slot.isBefore(LocalDateTime.now().plusHours(2))) return BookingResult.TooLate
        if (slot.dayOfWeek == DayOfWeek.SATURDAY || slot.dayOfWeek == DayOfWeek.SUNDAY) return BookingResult.ClinicClosed
        val id = UUID.randomUUID().toString()
        try {
            HttpClient.post("/bookings", BookingRequest(id, patientId, slot))
        } catch (e: IOException) {
            println("booking failed: $e")
            return BookingResult.Failed(retryable = true)
        } catch (e: Exception) {
            return BookingResult.Failed(retryable = false)
        }
        GlobalScope.launch(Dispatchers.IO) { AnalyticsSdk.send("booking_created", mapOf("id" to id)) }
        return BookingResult.Booked(id, "WAW-" + id.take(6).uppercase())
    }
}
