package katas.ex12

import katas.ex12.domain.SubmitResult
import katas.ex12.domain.checkInvoiceDate
import katas.ex12.domain.reimbursableAmount
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Tests for single steps: make them green before UseCasesTest.
class StepsTest {
    private val today = LocalDate.of(2026, 6, 15)

    @Test fun `invoice from today is ok`() = assertNull(checkInvoiceDate(today, today))
    @Test fun `invoice from tomorrow is in future`() = assertEquals(SubmitResult.InvoiceInFuture, checkInvoiceDate(today.plusDays(1), today))
    @Test fun `invoice exactly 90 days old is ok`() = assertNull(checkInvoiceDate(today.minusDays(90), today))
    @Test fun `invoice 91 days old is too old`() = assertEquals(SubmitResult.InvoiceTooOld, checkInvoiceDate(today.minusDays(91), today))

    @Test fun `whole amount when limit allows`() = assertEquals(300_00, reimbursableAmount(300_00, 1_000_00, 200_00))
    @Test fun `only the rest of the limit`() = assertEquals(100_00, reimbursableAmount(300_00, 1_000_00, 900_00))
    @Test fun `zero when limit used up`() = assertEquals(0, reimbursableAmount(300_00, 1_000_00, 1_000_00))
    @Test fun `never negative when usage exceeds limit`() = assertEquals(0, reimbursableAmount(300_00, 1_000_00, 1_200_00))
}
