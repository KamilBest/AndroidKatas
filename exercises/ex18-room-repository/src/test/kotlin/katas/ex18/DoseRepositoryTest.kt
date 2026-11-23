package katas.ex18

import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

// GIVEN: do not change. The fake behaves like Room: the Flow emits after every change.
class FakeDoseDao : DoseDao {
    val table = MutableStateFlow<List<DoseEntity>>(emptyList())
    val queries = mutableListOf<Pair<Instant, Instant>>()

    override fun observeBetween(from: Instant, to: Instant): Flow<List<DoseEntity>> {
        queries += from to to
        return table.map { all -> all.filter { it.scheduledAt >= from && it.scheduledAt < to }.sortedBy { it.scheduledAt } }
    }

    override suspend fun markTaken(id: String, at: Instant): Int {
        val dose = table.value.firstOrNull { it.id == id && it.takenAt == null } ?: return 0
        table.value = table.value.map { if (it == dose) it.copy(takenAt = at) else it }
        return 1
    }
}

class MutableClock(var now: Instant) : Clock() {
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId?): Clock = this
    override fun instant(): Instant = now
}

class DoseRepositoryTest {
    // 2026-06-01 10:00 in Warsaw (UTC+2)
    private val clock = MutableClock(Instant.parse("2026-06-01T08:00:00Z"))
    private val dao = FakeDoseDao()
    private val repo = DoseRepository(dao, clock)

    private fun at(localDateTime: String): Instant =
        java.time.LocalDateTime.parse(localDateTime).atZone(ZoneId.of("Europe/Warsaw")).toInstant()

    private fun entity(id: String, local: String, takenAtLocal: String? = null) =
        DoseEntity(id, "Ibuprom", 200, at(local), takenAtLocal?.let(::at))

    @Test fun `queries today in patient zone`() = runTest {
        repo.todayDoses().first()
        assertEquals(at("2026-06-01T00:00") to at("2026-06-02T00:00"), dao.queries.single())
    }

    @Test fun `maps entities to domain with status`() = runTest {
        dao.table.value = listOf(
            entity("taken", "2026-06-01T07:00", takenAtLocal = "2026-06-01T07:05"),
            entity("missed", "2026-06-01T08:30"),      // 10:00 - 08:30 > 1 h
            entity("pending", "2026-06-01T09:30"),     // still inside the 1 h window
            entity("later", "2026-06-01T20:00"),
            entity("tomorrow", "2026-06-02T08:00"),
        )
        assertEquals(
            listOf(
                Dose("taken", "Ibuprom 200 mg", LocalTime.of(7, 0), DoseStatus.TAKEN),
                Dose("missed", "Ibuprom 200 mg", LocalTime.of(8, 30), DoseStatus.MISSED),
                Dose("pending", "Ibuprom 200 mg", LocalTime.of(9, 30), DoseStatus.PENDING),
                Dose("later", "Ibuprom 200 mg", LocalTime.of(20, 0), DoseStatus.PENDING),
            ),
            repo.todayDoses().first(),
        )
    }

    @Test fun `take marks dose and second take reports already taken`() = runTest {
        dao.table.value = listOf(entity("d1", "2026-06-01T09:30"))
        assertEquals(TakeResult.Taken, repo.take("d1"))
        assertEquals(clock.now, dao.table.value.single().takenAt)
        assertEquals(TakeResult.AlreadyTaken, repo.take("d1"))
    }

    @Test fun `pending count updates after take and skips duplicates`() = runTest {
        dao.table.value = listOf(entity("d1", "2026-06-01T09:30"), entity("d2", "2026-06-01T20:00"))
        repo.pendingCount().test {
            assertEquals(2, awaitItem())
            repo.take("d1")
            assertEquals(1, awaitItem())
            dao.table.value = dao.table.value + entity("other-day", "2026-06-03T08:00")
            expectNoEvents()
        }
    }
}
