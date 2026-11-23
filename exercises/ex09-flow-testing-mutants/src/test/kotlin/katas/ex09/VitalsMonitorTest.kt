package katas.ex09

import app.cash.turbine.test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class VitalsMonitorTest {

    private val monitor = vitalsMonitor()

    // An example to start with: add the rest.
    @Test fun `no alerts for normal readings`() = runTest {
        monitor.alerts(flowOf(70, 80, 90)).test {
            awaitComplete()
        }
    }

    @Test fun `average of a single reading`() = runTest {
        assertEquals(listOf(70), monitor.averages(flowOf(70)).toList())
    }
}
