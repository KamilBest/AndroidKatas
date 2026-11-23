package katas.ex02

import java.time.LocalDate

// GIVEN: do not change
enum class Category { CONSULTATION, DENTAL, PHARMACY, HOSPITAL, PHYSIO }

data class Claim(
    val id: String,
    val patientId: String,
    val provider: String,
    val category: Category,
    val amountMinor: Long,
    val date: LocalDate,
)
