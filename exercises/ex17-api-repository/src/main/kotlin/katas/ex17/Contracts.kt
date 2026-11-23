package katas.ex17

import java.time.LocalTime

// GIVEN: "Retrofit": the API throws IOException (no network) or HttpError(code)
data class PharmacyDto(val id: String, val name: String, val openingHours: String?, val rating: Double?)
class HttpError(val code: Int) : Exception("HTTP $code")

interface PharmacyApi {
    suspend fun pharmacy(id: String): PharmacyDto
}

// GIVEN: domain
data class Pharmacy(val id: String, val name: String, val opensAt: LocalTime?, val closesAt: LocalTime?, val rating: Double?)

enum class DataError { Offline, NotFound, Server }

sealed interface Outcome<out T> {
    data class Success<T>(val value: T) : Outcome<T>
    data class Failure(val error: DataError) : Outcome<Nothing>
}
