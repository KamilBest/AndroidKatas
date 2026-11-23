package katas.bonus02

import java.time.LocalDate

enum class Sex { FEMALE, MALE }
enum class PeselError { WRONG_LENGTH, NOT_DIGITS, INVALID_DATE, WRONG_CHECKSUM }

sealed interface PeselResult {
    data class Valid(val birthDate: LocalDate, val sex: Sex) : PeselResult
    data class Invalid(val reason: PeselError) : PeselResult
}

object Pesel {
    fun parse(input: String): PeselResult = TODO()
}
