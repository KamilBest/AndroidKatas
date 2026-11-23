package katas.ex27

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow

// GIVEN: callback-based sensor API
fun interface HeartRateListener { fun onBpm(bpm: Int) }
interface HeartRateSensor {
    fun register(listener: HeartRateListener)
    fun unregister(listener: HeartRateListener)
}

/** 1. Shows "Loading…", then the result of `load(drugId)`. A new drugId cancels the previous load. */
@Composable
fun DrugInfo(drugId: String, load: suspend (String) -> String, modifier: Modifier = Modifier) {
    Text("TODO", modifier)
}

/** 2. Registers the listener while on screen, unregisters when it leaves. Shows "72 bpm" or "—". */
@Composable
fun SensorReadout(sensor: HeartRateSensor, modifier: Modifier = Modifier) {
    Text("TODO", modifier)
}

/** 3. After [timeoutMs] calls the **current** [onTimeout]. A new lambda does not restart the countdown. */
@Composable
fun AutoLogout(timeoutMs: Long, onTimeout: () -> Unit) {
}

/** 4. Collects one-time events only while the screen is at least STARTED. */
@Composable
fun EventsCollector(events: Flow<String>, onEvent: (String) -> Unit) {
}
