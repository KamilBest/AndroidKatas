package katas.ex28

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalTime

// GIVEN: a recomposition counter used by the tests
class RecompositionCounter {
    val counts = mutableMapOf<String, Int>()
    fun hit(tag: String) { counts[tag] = (counts[tag] ?: 0) + 1 }
}

data class Dose(val id: String, val name: String, val time: LocalTime)

// The code below WORKS, but it is slow. Fix it so the tests pass.
// Don't remove the `counter.hit(...)` lines: they are "sensors" for the tests.

/** 1. The "Back to top" button is visible when the list is scrolled. */
@Composable
fun DoseList(doses: List<Dose>, listState: LazyListState, counter: RecompositionCounter) {
    SideEffect { counter.hit("list") }
    val showScrollToTop = listState.firstVisibleItemIndex > 0
    Column {
        if (showScrollToTop) Text("Back to top")
        LazyColumn(state = listState) {
            items(doses) { dose -> Text(dose.name, Modifier.height(50.dp)) }
        }
    }
}

/** 2. Doses sorted by time. The highlight changes often, the list rarely. */
@Composable
fun SortedDoses(doses: List<Dose>, highlighted: Boolean, counter: RecompositionCounter) {
    val sorted = doses.sortedBy { it.time }.also { counter.hit("sort") }
    Column(Modifier.background(if (highlighted) Color.Yellow else Color.White)) {
        sorted.forEach { Text(it.name) }
    }
}

/** 3. A banner moved by an animation (the offset changes every frame). */
@Composable
fun SlidingBanner(offsetX: () -> Dp, counter: RecompositionCounter) {
    SideEffect { counter.hit("banner") }
    Box(Modifier.offset(x = offsetX())) {
        Text("Medication reminder")
    }
}
