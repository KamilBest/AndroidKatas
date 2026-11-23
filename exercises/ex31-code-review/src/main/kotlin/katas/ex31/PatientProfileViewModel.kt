package katas.ex31

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class ProfileUiState(
    val loading: Boolean = true,
    val profile: Profile? = null,
    val daysLeft: Long = 0,
    val error: String? = null,
)

@OptIn(DelicateCoroutinesApi::class)
class PatientProfileViewModel(private val context: Context) : ViewModel() {

    private val repository = ProfileRepository()
    val state = MutableStateFlow(ProfileUiState())

    fun load() {
        GlobalScope.launch {
            try {
                val token = TokenStorage.token(context)!!
                val profile = repository.loadProfile(token)
                state.value = state.value.copy(
                    loading = false,
                    profile = profile,
                    daysLeft = repository.daysToPolicyEnd(profile),
                )
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = e.toString())
            }
        }
    }

    fun logout() = runBlocking {
        context.getSharedPreferences("auth", Context.MODE_PRIVATE).edit().clear().apply()
    }
}
