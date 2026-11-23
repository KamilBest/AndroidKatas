package katas.ex12

import katas.ex12.data.InvoiceDto
import katas.ex12.data.toDomain
import katas.ex12.domain.*
import katas.ex12.presentation.formatLimits
import kotlinx.coroutines.test.runTest
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class FakePolicies(private val benefits: List<Benefit>) : PolicyRepository {
    override suspend fun benefitsFor(memberId: String) = benefits
}

class FakeUsage : UsageRepository {
    val used = mutableMapOf<Pair<BenefitType, Int>, Long>()
    val recorded = mutableListOf<Pair<Invoice, Long>>()
    override suspend fun usedInYear(memberId: String, type: BenefitType, year: Int) = used[type to year] ?: 0L
    override suspend fun record(memberId: String, invoice: Invoice, reimbursedMinor: Long) {
        recorded += invoice to reimbursedMinor
        used.merge(invoice.benefit to invoice.issuedOn.year, reimbursedMinor, Long::plus)
    }
}

class UseCasesTest {
    // 2026-12-31 23:30 UTC = 2027-01-01 00:30 in Warsaw → the "current year" is 2027!
    private val clock = Clock.fixed(Instant.parse("2026-12-31T23:30:00Z"), ZoneOffset.UTC)
    private val policies = FakePolicies(listOf(Benefit(BenefitType.DENTAL, 1000_00), Benefit(BenefitType.PHYSIO, 500_00)))
    private val usage = FakeUsage()
    private val submit = SubmitReimbursementUseCase(policies, usage, clock)
    private fun invoice(amount: Long, date: String = "2026-12-20", type: BenefitType = BenefitType.DENTAL) =
        Invoice("F/1", type, amount, LocalDate.parse(date))

    @Test fun `formats limits with display names`() {
        val limits = mapOf(BenefitType.DENTAL to 800_00L, BenefitType.PHYSIO to 0L)
        assertEquals(listOf("Dentistry: 800,00 zł", "Physiotherapy: 0,00 zł"), formatLimits(limits))
    }

    @Test fun `accepts and records reimbursement`() = runTest {
        assertEquals(SubmitResult.Accepted(300_00), submit("m1", invoice(300_00)))
        assertEquals(300_00L, usage.recorded.single().second)
    }

    @Test fun `partial reimbursement up to remaining limit`() = runTest {
        usage.used[BenefitType.DENTAL to 2027] = 900_00
        assertEquals(SubmitResult.Accepted(100_00), submit("m1", invoice(250_00)))
    }

    @Test fun `limit exhausted`() = runTest {
        usage.used[BenefitType.DENTAL to 2027] = 1000_00
        assertEquals(SubmitResult.LimitExhausted, submit("m1", invoice(10_00)))
        assertEquals(emptyList(), usage.recorded)
    }

    @Test fun `validation order and boundaries`() = runTest {
        assertEquals(SubmitResult.InvoiceInFuture, submit("m1", invoice(1, date = "2027-01-02")))
        assertEquals(SubmitResult.Accepted(1), submit("m1", invoice(1, date = "2027-01-01")))
        assertEquals(SubmitResult.Accepted(1), submit("m1", invoice(1, date = "2026-10-03"))) // exactly 90 days
        assertEquals(SubmitResult.InvoiceTooOld, submit("m1", invoice(1, date = "2026-10-02")))
        assertEquals(SubmitResult.NotCovered, submit("m1", invoice(1, type = BenefitType.GLASSES)))
        assertEquals(SubmitResult.InvoiceTooOld, submit("m1", invoice(1, date = "2020-01-01", type = BenefitType.GLASSES)))
    }

    @Test fun `dto maps to domain in data layer`() {
        assertEquals(
            Invoice("FV/7", BenefitType.PHYSIO, 123_45, LocalDate.parse("2026-01-31")),
            InvoiceDto("FV/7", "physio", "123.45", "2026-01-31").toDomain(),
        )
    }
}
