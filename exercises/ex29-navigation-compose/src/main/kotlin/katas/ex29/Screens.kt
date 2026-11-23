package katas.ex29

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

// GIVEN: routes and ready screens. Do not change.

@Serializable data object PrescriptionList
@Serializable data class PrescriptionDetail(val id: String)

@Composable
fun ListScreen(onOpen: (id: String) -> Unit, redeemedId: String?) {
    Column {
        Text("Your prescriptions")
        listOf("rx-1", "rx-2").forEach { id -> Button(onClick = { onOpen(id) }) { Text("Prescription $id") } }
        if (redeemedId != null) Text("Redeemed: $redeemedId")
    }
}

@Composable
fun DetailScreen(id: String, onRedeem: () -> Unit, onBack: () -> Unit) {
    Column {
        Text("Details $id")
        Button(onClick = onRedeem) { Text("Redeem") }
        Button(onClick = onBack) { Text("Back") }
    }
}
