package katas.ex25

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

// GIVEN: do not change
@RunWith(AndroidJUnit4::class)
class DoseScreenTest {
    @get:Rule val compose = createComposeRule()

    private val doses = listOf(
        DoseItem("d1", "Ibuprom", 200, LocalTime.of(8, 0), taken = true),
        DoseItem("d2", "Euthyrox", 50, LocalTime.of(6, 30), taken = false),
        DoseItem("d3", "Apap", 500, LocalTime.of(20, 15), taken = false),
    )

    @Test fun `shows header and rows`() {
        compose.setContent { DoseListScreen(DoseListUiState(doses), {}, {}) }
        compose.onNodeWithText("Today: 1/3 taken").assertIsDisplayed()
        compose.onNodeWithText("Euthyrox 50 mg · 06:30").assertIsDisplayed()
        compose.onNodeWithTag("dose-d1").assertIsOn()
        compose.onNodeWithTag("dose-d2").assertIsOff()
    }

    @Test fun `clicking a row reports its id`() {
        val clicked = mutableListOf<String>()
        compose.setContent { DoseListScreen(DoseListUiState(doses), { clicked += it }, {}) }
        compose.onNodeWithTag("dose-d3").performClick()
        assertEquals(listOf("d3"), clicked)
    }

    @Test fun `state hoisting - filter is driven from outside`() {
        var state by mutableStateOf(DoseListUiState(doses))
        compose.setContent { DoseListScreen(state, {}, { state = state.copy(onlyPending = it) }) }
        compose.onNodeWithTag("only-pending").assertIsOff().performClick()
        compose.onNodeWithTag("only-pending").assertIsOn()
        compose.onNodeWithTag("dose-d1").assertDoesNotExist()
        compose.onNodeWithText("Today: 1/3 taken").assertIsDisplayed()
    }

    @Test fun `empty state`() {
        compose.setContent { DoseListScreen(DoseListUiState(doses.map { it.copy(taken = true) }, onlyPending = true), {}, {}) }
        compose.onNodeWithText("No doses to show").assertIsDisplayed()
    }
}
