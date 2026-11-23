// MUTANT: boundary 60/61 moved
package katas.bonus04

import java.math.BigDecimal
import java.math.RoundingMode

internal class Mutant03 : PremiumCalculator {

    override fun quote(plan: Plan, applicant: Applicant, billing: Billing): Long {
        require(applicant.age >= ADULT_AGE) { "Applicant must be an adult" }
        require(applicant.dependents >= 0) { "Dependents cannot be negative" }
        val base = BigDecimal(plan.monthlyBaseMinor)
        val holder = base * ageFactor(applicant.age) * (if (applicant.smoker) SMOKER_FACTOR else BigDecimal.ONE)
        val dependents = base * DEPENDENT_SHARE * BigDecimal(minOf(applicant.dependents, MAX_DEPENDENTS))
        val monthly = (holder + dependents) * (BigDecimal.ONE - loyaltyDiscount(applicant.loyaltyYears))
        val total = when (billing) {
            Billing.MONTHLY -> monthly
            Billing.ANNUAL -> monthly * BigDecimal(MONTHS) * ANNUAL_FACTOR
        }
        return total.setScale(0, RoundingMode.HALF_UP).longValueExact()
    }

    private fun ageFactor(age: Int): BigDecimal = when {
        age < 26 -> BigDecimal("0.8")
        age in 26..45 -> BigDecimal.ONE
        age < 60 -> BigDecimal("1.4")
        else -> BigDecimal("2.0")
    }

    private fun loyaltyDiscount(years: Int): BigDecimal = when {
        years >= 5 -> BigDecimal("0.10")
        years >= 3 -> BigDecimal("0.05")
        else -> BigDecimal.ZERO
    }

    private companion object {
        const val ADULT_AGE = 18
        const val MAX_DEPENDENTS = 4
        const val MONTHS = 12
        val SMOKER_FACTOR = BigDecimal("1.25")
        val DEPENDENT_SHARE = BigDecimal("0.40")
        val ANNUAL_FACTOR = BigDecimal("0.95")
    }
}
