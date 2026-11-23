package katas.ex11

import katas.ex11.Intent.*
import kotlin.test.Test
import kotlin.test.assertEquals

class BookingReducerTest {
    private val doctors = listOf(Doctor("d1", "dr Nowak"), Doctor("d2", "dr Kowalska"))
    private val pickingSlot = BookingState(step = Step.PICK_SLOT, doctors = doctors, selectedDoctorId = "d1", slots = listOf("10:00", "11:00"))

    @Test fun `load doctors`() {
        assertEquals(Next(BookingState(isLoading = true), listOf(Effect.FetchDoctors)), reduce(BookingState(error = "x"), LoadDoctors))
        assertEquals(Next(BookingState(doctors = doctors)), reduce(BookingState(isLoading = true), DoctorsLoaded(doctors)))
        assertEquals(Next(BookingState(error = "offline")), reduce(BookingState(isLoading = true), LoadFailed("offline")))
    }

    @Test fun `select doctor fetches slots and resets selection`() {
        val next = reduce(pickingSlot.copy(selectedSlot = "10:00"), SelectDoctor("d2"))
        assertEquals(
            Next(BookingState(step = Step.PICK_SLOT, doctors = doctors, selectedDoctorId = "d2", isLoading = true), listOf(Effect.FetchSlots("d2"))),
            next,
        )
    }

    @Test fun `stale slots response is ignored`() {
        val state = pickingSlot.copy(selectedDoctorId = "d2", slots = emptyList(), isLoading = true)
        assertEquals(Next(state), reduce(state, SlotsLoaded("d1", listOf("09:00"))))
        assertEquals(listOf("12:00"), reduce(state, SlotsLoaded("d2", listOf("12:00"))).state.slots)
    }

    @Test fun `only listed slot can be selected`() {
        assertEquals("11:00", reduce(pickingSlot, SelectSlot("11:00")).state.selectedSlot)
        assertEquals(Next(pickingSlot), reduce(pickingSlot, SelectSlot("23:00")))
    }

    @Test fun `confirm books once`() {
        val ready = pickingSlot.copy(selectedSlot = "10:00")
        val first = reduce(ready, Confirm)
        assertEquals(Next(ready.copy(isSubmitting = true), listOf(Effect.Book("d1", "10:00"))), first)
        assertEquals(Next(first.state), reduce(first.state, Confirm), "double click must not book twice")
        assertEquals(Next(pickingSlot), reduce(pickingSlot, Confirm), "no slot selected")
    }

    @Test fun `booking result`() {
        val submitting = pickingSlot.copy(selectedSlot = "10:00", isSubmitting = true)
        assertEquals(Next(submitting.copy(isSubmitting = false), listOf(Effect.ShowConfirmation("WAW-1"))), reduce(submitting, Booked("WAW-1")))
        assertEquals(Next(submitting.copy(isSubmitting = false, error = "taken")), reduce(submitting, BookingFailed("taken")))
    }

    @Test fun `back navigation`() {
        assertEquals(Next(BookingState(doctors = doctors)), reduce(pickingSlot.copy(selectedSlot = "10:00"), Back))
        assertEquals(Next(BookingState(), listOf(Effect.Close)), reduce(BookingState(), Back))
    }
}
