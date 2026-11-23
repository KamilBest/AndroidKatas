package katas.ex01

sealed interface ClaimState {
    data object Draft : ClaimState
    data object Submitted : ClaimState
    data class UnderReview(val reviewer: String) : ClaimState
    data class NeedsInfo(val reviewer: String, val question: String) : ClaimState
    data class Approved(val amountMinor: Long) : ClaimState
    data class Rejected(val reason: String) : ClaimState
    data object Paid : ClaimState
}

sealed interface ClaimEvent {
    data object Submit : ClaimEvent
    data class AssignReviewer(val name: String) : ClaimEvent
    data class RequestInfo(val question: String) : ClaimEvent
    data class ProvideInfo(val answer: String) : ClaimEvent
    data class Approve(val amountMinor: Long) : ClaimEvent
    data class Reject(val reason: String) : ClaimEvent
    data object Pay : ClaimEvent
}

sealed interface Transition {
    data class Moved(val to: ClaimState) : Transition
    data class Ignored(val reason: String) : Transition
}

val ClaimState.isTerminal: Boolean get() = TODO()

object ClaimMachine {
    fun reduce(state: ClaimState, event: ClaimEvent): Transition = TODO()

    fun replay(events: List<ClaimEvent>): ClaimState = TODO()
}
