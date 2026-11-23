package katas.ex12.data

import katas.ex12.domain.Invoice

data class InvoiceDto(val number: String, val category: String, val grossAmount: String, val date: String)

/** category: "dental"/"physio"/"glasses", grossAmount: "123.45", date: ISO "2026-01-31". */
fun InvoiceDto.toDomain(): Invoice = TODO()
