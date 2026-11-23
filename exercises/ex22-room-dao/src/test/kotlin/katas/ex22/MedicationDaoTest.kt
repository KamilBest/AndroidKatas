package katas.ex22

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class MedicationDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: MedicationDao

    private val t0 = Instant.parse("2026-06-01T08:00:00Z")
    private fun at(hours: Long) = t0.plusSeconds(hours * 3600)
    private val ibuprom = MedicationEntity("m1", "ibuprom", 200)
    private val apap = MedicationEntity("m2", "Apap", 500)

    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.medicationDao()
    }

    @After fun tearDown() = db.close()

    @Test fun `upsert and observe sorted case insensitive`() = runTest {
        dao.observeMedications().test {
            assertEquals(emptyList(), awaitItem())
            dao.upsertMedication(ibuprom)
            assertEquals(listOf(ibuprom), awaitItem())
            dao.upsertMedication(apap)
            assertEquals(listOf(apap, ibuprom), awaitItem())
            dao.upsertMedication(ibuprom.copy(doseMg = 400))
            assertEquals(400, awaitItem().last().doseMg)
        }
    }

    @Test fun `doses between inclusive range`() = runTest {
        dao.upsertMedication(ibuprom)
        dao.insertDoses(listOf(DoseEntity("d3", "m1", at(8)), DoseEntity("d1", "m1", at(0)), DoseEntity("d2", "m1", at(4)), DoseEntity("d4", "m1", at(30))))
        assertEquals(listOf("d1", "d2", "d3"), dao.observeDosesBetween(at(0), at(8)).first().map { it.id })
    }

    @Test fun `mark taken only once`() = runTest {
        dao.upsertMedication(ibuprom)
        dao.insertDoses(listOf(DoseEntity("d1", "m1", at(0))))
        assertEquals(1, dao.markTaken("d1", at(1)))
        assertEquals(0, dao.markTaken("d1", at(2)))
        assertEquals(at(1), dao.observeDosesBetween(at(0), at(0)).first().single().takenAt)
        assertEquals(0, dao.markTaken("missing", at(1)))
    }

    @Test fun `replace future doses keeps past and taken ones`() = runTest {
        dao.upsertMedication(ibuprom)
        dao.insertDoses(
            listOf(
                DoseEntity("past", "m1", at(-2)),
                DoseEntity("futureTaken", "m1", at(2), takenAt = at(1)),
                DoseEntity("futureOpen", "m1", at(3)),
            ),
        )
        dao.replaceFutureDoses("m1", now = t0, doses = listOf(DoseEntity("new1", "m1", at(6)), DoseEntity("new2", "m1", at(12))))
        assertEquals(
            listOf("past", "futureTaken", "new1", "new2"),
            dao.observeDosesBetween(at(-10), at(100)).first().map { it.id },
        )
    }
}
