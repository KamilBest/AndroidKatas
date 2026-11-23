package katas.ex29

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.toRoute
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// GIVEN: do not change
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: TestNavHostController

    @Before fun setUp() {
        compose.setContent {
            nav = TestNavHostController(LocalContext.current).apply { navigatorProvider.addNavigator(ComposeNavigator()) }
            AppNavHost(nav)
        }
    }

    private fun detailEntries() = nav.currentBackStack.value.count { it.destination.hasRoute<PrescriptionDetail>() }

    @Test fun `starts on the list`() {
        compose.onNodeWithText("Your prescriptions").assertExists()
        assertTrue(nav.currentDestination!!.hasRoute<PrescriptionList>())
    }

    @Test fun `opens detail with typed argument`() {
        compose.onNodeWithText("Prescription rx-2").performClick()
        compose.onNodeWithText("Details rx-2").assertExists()
        assertEquals("rx-2", nav.currentBackStackEntry!!.toRoute<PrescriptionDetail>().id)
    }

    @Test fun `back returns to the list`() {
        compose.onNodeWithText("Prescription rx-1").performClick()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Your prescriptions").assertExists()
        assertEquals(0, detailEntries())
    }

    @Test fun `redeem returns a result to the list`() {
        compose.onNodeWithText("Prescription rx-2").performClick()
        compose.onNodeWithText("Redeem").performClick()
        compose.onNodeWithText("Redeemed: rx-2").assertExists()
        assertTrue(nav.currentDestination!!.hasRoute<PrescriptionList>())
    }
}
