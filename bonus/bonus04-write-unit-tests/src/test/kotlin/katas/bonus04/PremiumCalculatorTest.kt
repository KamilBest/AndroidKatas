package katas.bonus04

import kotlin.test.Test
import kotlin.test.assertEquals

class PremiumCalculatorTest {

    private val calculator = premiumCalculator()

    @Test fun `basic plan for a 30 year old`() {
        // 50 zł × 1.0 = 50.00 zł
        assertEquals(50_00, calculator.quote(Plan.BASIC, Applicant(age = 30)))
    }

    // TODO: add tests until mutationTest shows 13/13
}
