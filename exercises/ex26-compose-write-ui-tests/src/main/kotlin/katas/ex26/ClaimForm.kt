package katas.ex26

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.math.BigDecimal

// GIVEN: do not change. Your task is to write tests.

enum class Category(val label: String) { DOCTOR("Doctor"), DRUGS("Drugs"), REHAB("Rehab") }

data class ClaimDraft(val amountMinor: Long, val category: Category, val description: String)

private const val MAX_DESCRIPTION = 200
private val MAX_AMOUNT = BigDecimal("10000.00")

internal fun parseAmountMinor(input: String): Long? {
    val normalized = input.trim().replace(',', '.')
    if (!Regex("""\d+(\.\d{1,2})?""").matches(normalized)) return null
    val value = BigDecimal(normalized)
    if (value <= BigDecimal.ZERO || value > MAX_AMOUNT) return null
    return value.movePointRight(2).longValueExact()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimForm(onSubmit: (ClaimDraft) -> Unit, modifier: Modifier = Modifier) {
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<Category?>(null) }

    val amountMinor = parseAmountMinor(amount)
    val showAmountError = amount.isNotEmpty() && amountMinor == null
    val canSubmit = amountMinor != null && category != null && description.isNotBlank()

    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (zł)") },
            isError = showAmountError,
            supportingText = { if (showAmountError) Text("Enter an amount from 0,01 to 10 000 zł") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("amount"),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Category.entries.forEach { c ->
                FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c.label) })
            }
        }
        OutlinedTextField(
            value = description,
            onValueChange = { description = it.take(MAX_DESCRIPTION) },
            label = { Text("Description") },
            supportingText = { Text("${description.length}/$MAX_DESCRIPTION") },
            modifier = Modifier.fillMaxWidth().testTag("description"),
        )
        Button(
            onClick = { onSubmit(ClaimDraft(amountMinor!!, category!!, description.trim())) },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Submit claim") }
    }
}
