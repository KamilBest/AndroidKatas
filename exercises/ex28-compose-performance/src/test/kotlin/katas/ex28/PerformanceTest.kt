package katas.ex28

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.runner.RunWith
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// GIVEN: do not change
@RunWith(AndroidJUnit4::class)
class PerformanceTest {
    @get:Rule val compose = createComposeRule()
    private val counter = RecompositionCounter()
    private val doses = (1..100).map { Dose("d$it", "Drug $it", LocalTime.of(it % 24, 0)) }

    @Test fun `scrolling does not recompose the whole list on every item`() {
        val listState = LazyListState()
        lateinit var scope: CoroutineScope
        compose.setContent {
            scope = rememberCoroutineScope()
            DoseList(doses, listState, counter)
        }
        compose.onNodeWithText("Back to top").assertDoesNotExist()
        for (index in 1..10) {
            compose.runOnIdle { scope.launch { listState.scrollToItem(index) } }
            compose.waitForIdle()
        }
        compose.onNodeWithText("Back to top").assertExists()
        val count = counter.counts.getValue("list")
        assertTrue(count <= 2, "DoseList recomposed $count times for 10 scroll steps; expected at most 2")
    }

    @Test fun `sorting runs once when only highlight changes`() {
        var highlighted by mutableStateOf(false)
        compose.setContent { SortedDoses(doses.take(5), highlighted, counter) }
        repeat(3) {
            highlighted = !highlighted
            compose.waitForIdle()
        }
        assertEquals(1, counter.counts["sort"], "sorted again on every recomposition")
    }

    @Test fun `animated offset is read in layout, not in composition`() {
        var offset by mutableStateOf(0.dp)
        compose.setContent { SlidingBanner(offsetX = { offset }, counter = counter) }
        for (x in listOf(10.dp, 20.dp, 30.dp, 40.dp)) {
            offset = x
            compose.waitForIdle()
        }
        compose.onNodeWithText("Medication reminder").assertLeftPositionInRootIsEqualTo(40.dp)
        assertEquals(1, counter.counts["banner"], "banner recomposed on every frame of the animation")
    }
}
