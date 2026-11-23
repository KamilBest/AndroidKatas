package katas.ex12.domain

import java.time.Clock
import java.time.LocalDate

class SubmitReimbursementUseCase(
    private val policies: PolicyRepository,
    private val usage: UsageRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(memberId: String, invoice: Invoice): SubmitResult = TODO()
}

/** Step 1: `null` when the date is OK, otherwise `InvoiceInFuture` or `InvoiceTooOld` (older than 90 days from `today`). */
fun checkInvoiceDate(issuedOn: LocalDate, today: LocalDate): SubmitResult? = TODO()

/** Step 2: how much to refund: what is left of the limit, but not more than the invoice amount and never less than 0. */
fun reimbursableAmount(amountMinor: Long, annualLimitMinor: Long, usedMinor: Long): Long = TODO()
