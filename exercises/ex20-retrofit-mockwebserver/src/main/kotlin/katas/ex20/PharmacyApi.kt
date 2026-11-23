package katas.ex20

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Response

interface PharmacyApi {
    // TODO: Retrofit annotations
    suspend fun pharmacies(city: String, page: Int): PharmaciesPage
    suspend fun pharmacy(id: String): PharmacyDto
    suspend fun reserve(prescriptionId: String, body: ReserveRequest): Response<ReserveResponse>
}

fun createPharmacyApi(baseUrl: HttpUrl, client: OkHttpClient = OkHttpClient()): PharmacyApi = TODO()

sealed interface ReserveResult {
    data class Reserved(val pickupCode: String) : ReserveResult
    data object AlreadyReserved : ReserveResult
    data object NotFound : ReserveResult
    data object Offline : ReserveResult
    data class Failed(val httpCode: Int) : ReserveResult
}

class PharmacyRemoteDataSource(private val api: PharmacyApi) {
    /** The first page of pharmacies in a city. */
    suspend fun pharmaciesIn(city: String): List<PharmacyDto> = TODO()
    suspend fun findPharmacy(id: String): PharmacyDto? = TODO()
    suspend fun reserve(prescriptionId: String, pharmacyId: String, items: List<String>): ReserveResult = TODO()
}
