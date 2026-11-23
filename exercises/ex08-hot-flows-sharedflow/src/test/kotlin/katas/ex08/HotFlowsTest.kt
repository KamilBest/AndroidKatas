package katas.ex08

import app.cash.turbine.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HotFlowsTest {

    @Test fun `session events broadcast to all current subscribers, no replay`() = runTest {
        val bus = SessionEvents()
        bus.publish(SessionEvent.PolicyUpdated("old")) // nobody is listening
        val a = mutableListOf<SessionEvent>()
        val b = mutableListOf<SessionEvent>()
        val jobA = launch(UnconfinedTestDispatcher(testScheduler)) { bus.events.collect { a += it } }
        val jobB = launch(UnconfinedTestDispatcher(testScheduler)) { bus.events.collect { b += it } }
        assertTrue(bus.publish(SessionEvent.SessionExpired))
        assertEquals(listOf<SessionEvent>(SessionEvent.SessionExpired), a)
        assertEquals(a, b)
        jobA.cancel(); jobB.cancel()
    }

    @Test fun `tryEmit buffers for slow collector and drops oldest on overflow`() = runTest {
        val bus = SessionEvents()
        val received = mutableListOf<SessionEvent>()
        val job = launch { bus.events.collect { delay(1_000); received += it } }
        runCurrent()
        repeat(20) { assertTrue(bus.publish(SessionEvent.PolicyUpdated("p$it")), "publish #$it rejected") }
        advanceTimeBy(60_000)
        // the collector has not received anything yet: the 16 newest stay (p4..p19), the oldest were dropped
        assertEquals<List<SessionEvent>>((4..19).map { SessionEvent.PolicyUpdated("p$it") }, received)
        job.cancel()
    }

    @Test fun `download queue delivers each job to exactly one worker`() = runTest {
        val queue = DownloadQueue()
        val byWorker = List(3) { mutableListOf<String>() }
        val workers = byWorker.map { sink -> launch { queue.jobs.collect { delay(100); sink += it } } }
        repeat(9) { queue.enqueue("f$it") }
        advanceTimeBy(1_000)
        val all = byWorker.flatten()
        assertEquals((0 until 9).map { "f$it" }.toSet(), all.toSet())
        assertEquals(9, all.size, "no duplicates")
        assertTrue(byWorker.all { it.isNotEmpty() }, "work should be spread: $byWorker")
        workers.forEach { it.cancel() }
    }

    @Test fun `heart rate upstream is shared and stops after timeout`() = runTest {
        var starts = 0
        var active = false
        val sensor = flow { var bpm = 60; while (true) { emit(bpm++); delay(700) } } // 60@0, 61@700, 62@1400, 63@2100…
            .onStart { starts++; active = true }
            .onCompletion { active = false }
        val hub = HeartRateHub(sensor, backgroundScope)

        hub.readings.test { assertEquals(60, awaitItem()); cancelAndIgnoreRemainingEvents() }
        hub.readings.test { assertEquals(60, awaitItem()); cancelAndIgnoreRemainingEvents() } // replay, the same upstream
        assertEquals(1, starts)
        advanceTimeBy(1_500)
        assertTrue(active, "upstream should survive short gaps (e.g. rotation)")
        advanceTimeBy(1_000)
        assertTrue(!active, "upstream should stop 2s after last subscriber")
        assertEquals(listOf(62), hub.readings.take(1).toList()) // replay: the last value before stopping
    }

    @Test fun `state flow conflates - slow collector sees only latest`() = runTest {
        val state = MutableStateFlow(0)
        val seen = mutableListOf<Int>()
        val job = launch { state.collect { seen += it; delay(100) } }
        runCurrent()
        (1..5).forEach { state.value = it }
        advanceTimeBy(1_000)
        // TODO: think before you run it: what will the collector see?
        assertEquals(listOf(0, 5), seen)
        job.cancel()
    }
}
