package katas.ex27

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// GIVEN: do not change
@RunWith(AndroidJUnit4::class)
class SideEffectsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `drug info reloads on key change and cancels the old load`() {
        val pending = mutableMapOf<String, CompletableDeferred<String>>()
        val cancelled = mutableListOf<String>()
        val load: suspend (String) -> String = { id ->
            val d = CompletableDeferred<String>().also { pending[id] = it }
            try { d.await() } catch (e: CancellationException) { cancelled += id; throw e }
        }
        var drugId by mutableStateOf("apap")
        compose.setContent { DrugInfo(drugId, load) }
        compose.onNodeWithText("Loading…").assertExists()

        drugId = "ibuprom"
        compose.waitForIdle()
        assertEquals(listOf("apap"), cancelled)
        compose.onNodeWithText("Loading…").assertExists()

        compose.runOnIdle { pending.getValue("ibuprom").complete("Ibuprom: painkiller") }
        compose.onNodeWithText("Ibuprom: painkiller").assertExists()
    }

    @Test fun `sensor listener is removed when composable leaves`() {
        val sensor = object : HeartRateSensor {
            val listeners = mutableListOf<HeartRateListener>()
            override fun register(listener: HeartRateListener) { listeners += listener }
            override fun unregister(listener: HeartRateListener) { listeners -= listener }
        }
        var visible by mutableStateOf(true)
        compose.setContent { if (visible) SensorReadout(sensor) }
        compose.onNodeWithText("—").assertExists()
        assertEquals(1, sensor.listeners.size)

        compose.runOnIdle { sensor.listeners.single().onBpm(72) }
        compose.onNodeWithText("72 bpm").assertExists()

        visible = false
        compose.waitForIdle()
        assertTrue(sensor.listeners.isEmpty(), "listener leaked")
    }

    @Test fun `auto logout calls the latest callback without restarting the timer`() {
        compose.mainClock.autoAdvance = false
        val calls = mutableListOf<String>()
        var callback by mutableStateOf({ calls += "old" })
        compose.setContent { AutoLogout(timeoutMs = 10_000, onTimeout = callback) }

        compose.mainClock.advanceTimeBy(6_000)
        callback = { calls += "new" }
        Snapshot.sendApplyNotifications() // a write outside composition: without it, recomposition would not see the new callback before the timer ends
        compose.mainClock.advanceTimeBy(5_000) // 11 s from the start: the timer must not have restarted
        assertEquals(listOf("new"), calls)
    }

    @Test fun `events are collected only while started`() {
        val owner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        val channel = Channel<String>(Channel.BUFFERED)
        val events = channel.receiveAsFlow()
        val received = mutableListOf<String>()
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                EventsCollector(events) { received += it }
            }
        }
        compose.runOnIdle { channel.trySend("first") }
        compose.waitForIdle()
        assertEquals(listOf("first"), received)

        compose.runOnIdle { owner.currentState = Lifecycle.State.CREATED } // app in the background
        compose.runOnIdle { channel.trySend("while-stopped") }
        compose.waitForIdle()
        assertEquals(listOf("first"), received, "must not handle events in background")

        compose.runOnIdle { owner.currentState = Lifecycle.State.RESUMED }
        compose.waitForIdle()
        assertEquals(listOf("first", "while-stopped"), received, "event waits in the channel and is delivered on return")
    }
}
