package katas.ex16

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

// GIVEN: app classes. Only add Hilt annotations and modules (in Modules.kt). Don't change the logic.

interface PrescriptionApi {
    suspend fun prescriptions(): List<String>
}

/** The "real" API (in the app: Retrofit). */
class InMemoryPrescriptionApi : PrescriptionApi {
    override suspend fun prescriptions() = listOf("Amotaks 1000 mg", "Euthyrox 50 µg")
}

interface PrescriptionsRepository {
    suspend fun all(): List<String>
}

class DefaultPrescriptionsRepository(
    private val api: PrescriptionApi,
    private val io: CoroutineDispatcher,
) : PrescriptionsRepository {
    val createdAt: Long = System.nanoTime()
    override suspend fun all(): List<String> = withContext(io) { api.prescriptions().sorted() }
}

/** Formats the list header. A new instance every time is fine (stateless). */
class HeaderFormatter {
    fun header(count: Int) = "Prescriptions: $count"
}

class PrescriptionsViewModel(
    private val repository: PrescriptionsRepository,
    private val formatter: HeaderFormatter,
) : androidx.lifecycle.ViewModel() {
    suspend fun header(): String = formatter.header(repository.all().size)
}
