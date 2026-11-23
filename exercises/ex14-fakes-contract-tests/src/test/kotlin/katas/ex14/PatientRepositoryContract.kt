package katas.ex14

// GIVEN: do not change. A contract for EVERY PatientRepository implementation.

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

abstract class PatientRepositoryContract {

    abstract fun createRepository(): PatientRepository

    private val ania = Patient("1", "44051401359", "Ania")
    private val bartek = Patient("2", "02270806221", "Bartek")

    @Test fun `saved patient can be found by id and pesel`() = runTest {
        val repo = createRepository()
        repo.save(ania)
        assertEquals(ania, repo.findById("1"))
        assertEquals(ania, repo.findByPesel("44051401359"))
        assertNull(repo.findById("nope"))
        assertNull(repo.findByPesel("nope"))
    }

    @Test fun `save with existing id updates`() = runTest {
        val repo = createRepository()
        repo.save(ania)
        repo.save(ania.copy(name = "Anna"))
        assertEquals("Anna", repo.findById("1")?.name)
    }

    @Test fun `observeAll emits sorted list after every change`() = runTest {
        val repo = createRepository()
        repo.observeAll().test {
            assertEquals(emptyList(), awaitItem())
            repo.save(bartek)
            assertEquals(listOf(bartek), awaitItem())
            repo.save(ania)
            assertEquals(listOf(ania, bartek), awaitItem())
            repo.delete("2")
            assertEquals(listOf(ania), awaitItem())
        }
    }

    @Test fun `deleting missing patient is a no-op`() = runTest {
        val repo = createRepository()
        repo.save(ania)
        repo.delete("missing")
        assertEquals(ania, repo.findById("1"))
    }
}
