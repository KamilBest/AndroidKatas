package katas.ex12.domain

import java.time.LocalDate

// GIVEN
enum class BenefitType { DENTAL, PHYSIO, GLASSES }
data class Benefit(val type: BenefitType, val annualLimitMinor: Long)
data class Invoice(val id: String, val benefit: BenefitType, val amountMinor: Long, val issuedOn: LocalDate)

interface PolicyRepository { suspend fun benefitsFor(memberId: String): List<Benefit> }
interface UsageRepository {
    suspend fun usedInYear(memberId: String, type: BenefitType, year: Int): Long
    suspend fun record(memberId: String, invoice: Invoice, reimbursedMinor: Long)
}

sealed interface SubmitResult {
    data class Accepted(val reimbursedMinor: Long) : SubmitResult
    data object InvoiceInFuture : SubmitResult
    data object InvoiceTooOld : SubmitResult
    data object NotCovered : SubmitResult
    data object LimitExhausted : SubmitResult
}
