package katas.ex30

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals

// GIVEN: tests for single steps. Start with them, then TakeHomeTest.
class StepsTest {
    // 23:30 UTC 11.03 = 00:30 12.03 in Warsaw
    private val clock: Clock = Clock.fixed(Instant.parse("2026-03-11T23:30:00Z"), ZoneId.of("UTC"))
    private val today = LocalDate.parse("2026-03-12")
    private fun rx(id: String, until: String, redeemed: Boolean = false) =
        Prescription(id, "Amotaks", "1000 mg", LocalDate.parse(until), redeemed)

    @Test fun `today is computed in Warsaw`() {
        assertEquals(today, todayInWarsaw(clock))
    }

    @Test fun `badge`() {
        assertEquals(Badge.ACTIVE, badgeFor(rx("1", "2026-03-12"), today), "valid until today means still active")
        assertEquals(Badge.EXPIRED, badgeFor(rx("1", "2026-03-11"), today))
        assertEquals(Badge.REDEEMED, badgeFor(rx("1", "2026-03-01", redeemed = true), today), "redeemed wins over expired")
    }

    @Test fun `list item texts`() {
        assertEquals(PrescriptionItem("1", "Amotaks 1000 mg", "Valid until 05.04.2026", Badge.ACTIVE), rx("1", "2026-04-05").toItem(clock))
    }

    @Test fun `sort order`() {
        val sorted = sortedForList(
            listOf(rx("expired", "2026-03-01"), rx("redeemed", "2026-05-01", true), rx("later", "2026-06-01"), rx("soon", "2026-03-20")),
            today,
        )
        assertEquals(listOf("soon", "later", "redeemed", "expired"), sorted.map { it.id })
    }

    @Test fun `screen state from data and refresh status`() {
        val data = listOf(rx("1", "2026-04-01"))
        assertEquals(ListUiState.Loading, listStateFor(data, RefreshStatus(initialLoad = true), clock))
        assertEquals(ListUiState.Empty, listStateFor(emptyList(), RefreshStatus(initialLoad = false), clock))
        assertEquals(ListUiState.Error("Could not load prescriptions"), listStateFor(emptyList(), RefreshStatus(initialLoad = false, failed = true), clock))
        assertEquals(
            ListUiState.Content(listOf(data.single().toItem(clock)), isRefreshing = true),
            listStateFor(data, RefreshStatus(initialLoad = false, refreshing = true, failed = true), clock),
            "failed refresh with data still shows content",
        )
    }
}
