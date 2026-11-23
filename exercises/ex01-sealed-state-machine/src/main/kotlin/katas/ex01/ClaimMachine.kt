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

val ClaimState.isTerminal: Boolean
    get() = when (this) {
        ClaimState.Paid, is ClaimState.Rejected -> true
        ClaimState.Draft, ClaimState.Submitted, is ClaimState.UnderReview,
        is ClaimState.NeedsInfo, is ClaimState.Approved -> false
    }

object ClaimMachine {
    fun reduce(state: ClaimState, event: ClaimEvent): Transition =
        when (state) {
            ClaimState.Draft ->
                if (event == ClaimEvent.Submit) Transition.Moved(ClaimState.Submitted)
                else Transition.Ignored("A draft can only be submitted")

            ClaimState.Submitted ->
                if (event is ClaimEvent.AssignReviewer) Transition.Moved(
                    ClaimState.UnderReview(
                        event.name
                    )
                )
                else Transition.Ignored("Waiting for a reviewer")

            is ClaimState.UnderReview -> reduceUnderReview(state, event)

            is ClaimState.NeedsInfo ->
                if (event is ClaimEvent.ProvideInfo) Transition.Moved(ClaimState.UnderReview(state.reviewer))
                else Transition.Ignored("Waiting for an answer")

            is ClaimState.Approved ->
                if (event == ClaimEvent.Pay) Transition.Moved(ClaimState.Paid)
                else Transition.Ignored("An approved claim can only be paid")

            ClaimState.Paid, is ClaimState.Rejected ->
                Transition.Ignored("The claim is closed")
        }

    private fun reduceUnderReview(state: ClaimState.UnderReview, event: ClaimEvent): Transition =
        when (event) {
            is ClaimEvent.RequestInfo -> Transition.Moved(
                ClaimState.NeedsInfo(
                    state.reviewer,
                    event.question
                )
            )

            is ClaimEvent.Approve ->
                if (event.amountMinor > 0) Transition.Moved(ClaimState.Approved(event.amountMinor))
                else Transition.Ignored("Amount must be greater than 0")

            is ClaimEvent.Reject ->
                if (event.reason.isNotBlank()) Transition.Moved(ClaimState.Rejected(event.reason))
                else Transition.Ignored("Rejection needs a reason")

            else -> Transition.Ignored("Only Approve, Reject or RequestInfo here")
        }

    fun replay(events: List<ClaimEvent>): ClaimState {
        val start: ClaimState = ClaimState.Draft
        return events.fold(start) { state, event ->
            when (val transition = reduce(state, event)) {
                is Transition.Moved -> transition.to
                is Transition.Ignored -> state
            }
        }
    }
}

