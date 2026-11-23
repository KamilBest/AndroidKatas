package katas.ex14

// GIVEN: do not change.

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterPatientUseCaseTest {
    private val repository = InMemoryPatientRepository()
    private val register = RegisterPatientUseCase(repository, newId = { "id-1" })

    @Test fun `registers new patient with trimmed name`() = runTest {
        val result = register("44051401359", "  Ania ")
        assertEquals(RegisterResult.Registered(Patient("id-1", "44051401359", "Ania")), result)
        assertEquals(listOf(Patient("id-1", "44051401359", "Ania")), repository.observeAll().first())
    }

    @Test fun `rejects duplicate pesel`() = runTest {
        repository.save(Patient("x", "44051401359", "Someone"))
        assertEquals(RegisterResult.PeselTaken, register("44051401359", "Ania"))
    }

    @Test fun `rejects blank name without touching repository`() = runTest {
        assertEquals(RegisterResult.InvalidName, register("44051401359", "   "))
        assertEquals(emptyList(), repository.observeAll().first())
    }
}
