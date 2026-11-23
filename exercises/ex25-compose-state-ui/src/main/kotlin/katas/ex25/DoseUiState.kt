package katas.ex25

import androidx.compose.runtime.Immutable
import java.time.LocalTime

// GIVEN
@Immutable
data class DoseItem(val id: String, val name: String, val doseMg: Int, val time: LocalTime, val taken: Boolean)

@Immutable
data class DoseListUiState(val doses: List<DoseItem> = emptyList(), val onlyPending: Boolean = false) {
    val takenCount: Int get() = doses.count { it.taken }
    val visible: List<DoseItem> get() = if (onlyPending) doses.filterNot { it.taken } else doses
}
