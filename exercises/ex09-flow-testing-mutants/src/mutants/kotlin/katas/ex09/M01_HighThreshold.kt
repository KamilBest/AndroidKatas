// MUTANT: HIGH boundary: ">= 120" instead of "> 120"
package katas.ex09

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class Mutant01 : VitalsMonitor {

    override fun alerts(readings: Flow<Int>): Flow<Alert> = flow {
        var consecutiveHigh = 0
        var highActive = false
        var lowActive = false
        readings.collect { bpm ->
            if (bpm !in VALID_RANGE) return@collect
            if (bpm >= HIGH_BPM) consecutiveHigh++ else consecutiveHigh = 0
            if (consecutiveHigh >= HIGH_STREAK && !highActive) {
                highActive = true
                emit(Alert.HIGH)
            }
            if (bpm <= HIGH_BPM) highActive = false
            if (bpm < LOW_BPM && !lowActive) {
                lowActive = true
                emit(Alert.LOW)
            }
            if (bpm >= LOW_BPM) lowActive = false
        }
    }

    override fun averages(readings: Flow<Int>): Flow<Int> = flow {
        val window = ArrayDeque<Int>()
        readings.collect { bpm ->
            if (bpm !in VALID_RANGE) return@collect
            window.addLast(bpm)
            if (window.size > WINDOW) window.removeFirst()
            emit(Math.round(window.average()).toInt())
        }
    }

    private companion object {
        val VALID_RANGE = 20..250
        const val HIGH_BPM = 120
        const val HIGH_STREAK = 3
        const val LOW_BPM = 40
        const val WINDOW = 5
    }
}
