package katas.ex31

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PatientProfileScreen(viewModel: PatientProfileViewModel, onLoggedOut: () -> Unit) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        viewModel.load()
    }

    Column(Modifier.padding(16.dp)) {
        if (state.loading) Text("Loading…")
        state.error?.let { Text("Error: $it") }
        state.profile?.let { profile ->
            Text(profile.name)
            Text("PESEL: ${profile.pesel}")
            Text("Policy ${profile.policyNumber}, ${state.daysLeft} days left")
        }
        Text(
            "Log out",
            modifier = Modifier.clickable {
                viewModel.logout()
                onLoggedOut()
            },
        )
    }
}
