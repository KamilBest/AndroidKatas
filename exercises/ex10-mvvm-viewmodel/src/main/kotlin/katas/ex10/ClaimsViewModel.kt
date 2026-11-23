package katas.ex10

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ClaimsViewModel(private val repository: ClaimsRepository) : ViewModel() {

    val state: StateFlow<ClaimsUiState> = TODO()
    val events: Flow<ClaimsEvent> = TODO()

    fun onRefresh(): Unit = TODO()
    fun onRetry(): Unit = TODO()
    fun onClaimClicked(id: String): Unit = TODO()
}
