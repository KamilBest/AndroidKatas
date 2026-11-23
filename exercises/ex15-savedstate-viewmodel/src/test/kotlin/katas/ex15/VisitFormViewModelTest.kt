package katas.ex15

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeVisitApi : VisitApi {
    val requests = mutableListOf<VisitRequest>()
    var gate: CompletableDeferred<Unit>? = null
    var fail = false
    override suspend fun requestVisit(request: VisitRequest): String {
        requests += request
        gate?.await()
        if (fail) throw IOException("No network")
        return "visit-1"
    }
}

class VisitFormViewModelTest {
    @get:Rule val main = MainDispatcherRule()
    private val api = FakeVisitApi()
    private val handle = SavedStateHandle(mapOf("doctorId" to "dr-7"))

    private fun vm() = VisitFormViewModel(handle, api)

    @Test fun `requires doctorId nav argument`() {
        assertFailsWith<IllegalStateException> { VisitFormViewModel(SavedStateHandle(), api) }
        assertEquals("dr-7", vm().doctorId)
    }

    @Test fun `validation`() = runTest {
        val vm = vm()
        vm.onSymptomsChange("ache")
        vm.onSinceDaysChange(400)
        runCurrent()
        with(vm.state.value) {
            assertTrue(symptomsError); assertTrue(sinceDaysError); assertFalse(canSubmit)
        }
        vm.onSymptomsChange("  headache since morning  ")
        vm.onSinceDaysChange(365)
        runCurrent()
        with(vm.state.value) {
            assertFalse(symptomsError); assertFalse(sinceDaysError); assertTrue(canSubmit)
        }
    }

    @Test fun `empty form shows no errors yet`() = runTest {
        val state = vm().state.value
        assertFalse(state.symptomsError); assertFalse(state.sinceDaysError); assertFalse(state.canSubmit)
    }

    @Test fun `form survives process death`() = runTest {
        val first = vm()
        first.onSymptomsChange("cough and fever")
        first.onSinceDaysChange(3)
        first.onUrgentChange(true)
        runCurrent()

        val restored = VisitFormViewModel(handle, api) // new process, the same saved state
        runCurrent()
        with(restored.state.value) {
            assertEquals("cough and fever", symptoms)
            assertEquals(3, sinceDays)
            assertTrue(urgent)
            assertTrue(canSubmit)
        }
    }

    @Test fun `submitting flag is not persisted`() = runTest {
        val vm = vm()
        vm.onSymptomsChange("cough and fever"); vm.onSinceDaysChange(3)
        api.gate = CompletableDeferred()
        vm.submit()
        runCurrent()
        assertTrue(vm.state.value.isSubmitting)
        val afterDeath = VisitFormViewModel(handle, api)
        runCurrent()
        assertFalse(afterDeath.state.value.isSubmitting)
        assertTrue(afterDeath.state.value.canSubmit)
    }

    @Test fun `submit sends trimmed request once, clears form, emits event`() = runTest {
        val vm = vm()
        vm.onSymptomsChange("  cough and fever "); vm.onSinceDaysChange(3)
        vm.events.test {
            vm.submit(); vm.submit()
            assertEquals(VisitEvent.Submitted("visit-1"), awaitItem())
        }
        assertEquals(listOf(VisitRequest("dr-7", "cough and fever", 3, false)), api.requests)
        advanceUntilIdle()
        assertEquals("", vm.state.value.symptoms)
        assertEquals(null, handle.get<Int>("sinceDays"))
    }

    @Test fun `failure keeps form and reports error`() = runTest {
        api.fail = true
        val vm = vm()
        vm.onSymptomsChange("cough and fever"); vm.onSinceDaysChange(3)
        vm.events.test {
            vm.submit()
            assertEquals(VisitEvent.Error("No network"), awaitItem())
        }
        advanceUntilIdle()
        assertEquals("cough and fever", vm.state.value.symptoms)
        assertFalse(vm.state.value.isSubmitting)
    }

    @Test fun `invalid form does not submit`() = runTest {
        val vm = vm()
        vm.onSymptomsChange("short")
        vm.submit()
        advanceUntilIdle()
        assertTrue(api.requests.isEmpty())
    }
}
