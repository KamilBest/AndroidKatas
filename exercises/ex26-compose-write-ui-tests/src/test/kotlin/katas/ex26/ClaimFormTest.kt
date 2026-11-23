package katas.ex26

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class ClaimFormTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `submit is disabled initially`() {
        compose.setContent { ClaimForm(onSubmit = {}) }
        compose.onNodeWithText("Submit claim").assertIsNotEnabled()
    }

    // TODO: more tests (see README)
}
