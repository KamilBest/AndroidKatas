package katas.bonus02

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.time.LocalDate
import kotlin.test.assertEquals

// GIVEN: do not change. An example of a parameterized test in JUnit4.
@RunWith(Parameterized::class)
class PeselValidTest(private val pesel: String, private val date: String, private val sex: Sex) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0} → {1} {2}")
        fun data() = listOf(
            arrayOf("44051401359", "1944-05-14", Sex.MALE),
            arrayOf("02270806221", "2002-07-08", Sex.FEMALE),
            arrayOf("99923100113", "1899-12-31", Sex.MALE),
            arrayOf("00410100048", "2100-01-01", Sex.FEMALE),
            arrayOf("00222901239", "2000-02-29", Sex.MALE),
            arrayOf("90022807707", "1990-02-28", Sex.FEMALE),
        )
    }

    @Test fun `parses valid pesel`() {
        assertEquals(PeselResult.Valid(LocalDate.parse(date), sex), Pesel.parse(pesel))
    }
}

@RunWith(Parameterized::class)
class PeselInvalidTest(private val pesel: String, private val error: PeselError) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: \"{0}\" → {1}")
        fun data() = listOf(
            arrayOf("", PeselError.WRONG_LENGTH),
            arrayOf("4405140135", PeselError.WRONG_LENGTH),
            arrayOf("440514013599", PeselError.WRONG_LENGTH),
            arrayOf("4405140135a", PeselError.NOT_DIGITS),
            arrayOf("44 51401359", PeselError.NOT_DIGITS),
            arrayOf("01222901236", PeselError.INVALID_DATE),   // 2001-02-29
            arrayOf("90130101234", PeselError.INVALID_DATE),   // month 13
            arrayOf("90023001230", PeselError.INVALID_DATE),   // 30 February
            arrayOf("44051401358", PeselError.WRONG_CHECKSUM),
            arrayOf("44051401350", PeselError.WRONG_CHECKSUM),
        )
    }

    @Test fun `reports first error`() {
        assertEquals(PeselResult.Invalid(error), Pesel.parse(pesel))
    }
}
