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
        ClaimState.Paid -> true
        is ClaimState.Rejected -> true
        is ClaimState.Approved -> false
        ClaimState.Draft -> false
        is ClaimState.NeedsInfo -> false
        ClaimState.Submitted -> false
        is ClaimState.UnderReview -> false
    }

object ClaimMachine {
    fun reduce(state: ClaimState, event: ClaimEvent): Transition =
        when (state) {
            ClaimState.Draft ->
                if (event == ClaimEvent.Submit) Transition.Moved(ClaimState.Submitted)
                else Transition.Ignored("A draft can only be submitted (Submit)")

            ClaimState.Submitted ->
                if (event is ClaimEvent.AssignReviewer) Transition.Moved(ClaimState.UnderReview(event.name))
                else Transition.Ignored("A submitted claim is waiting for a reviewer (AssignReviewer)")

            is ClaimState.UnderReview -> handleUnderReviewState(state, event)

            is ClaimState.NeedsInfo ->
                if (event is ClaimEvent.ProvideInfo) Transition.Moved(ClaimState.UnderReview(state.reviewer))
                else Transition.Ignored("The claim is waiting for more information (ProvideInfo)")

            is ClaimState.Approved ->
                if (event == ClaimEvent.Pay) Transition.Moved(ClaimState.Paid)
                else Transition.Ignored("An approved claim can only be paid (Pay)")

            ClaimState.Paid, is ClaimState.Rejected ->
                Transition.Ignored("The claim is closed (paid or rejected), this is a final state")
        }

    private fun handleUnderReviewState(
        state: ClaimState.UnderReview,
        event: ClaimEvent
    ): Transition {
        return when (event) {
            is ClaimEvent.RequestInfo -> Transition.Moved(
                ClaimState.NeedsInfo(
                    question = event.question,
                    reviewer = state.reviewer
                )
            )

            is ClaimEvent.Approve ->
                if (event.amountMinor > 0) Transition.Moved(ClaimState.Approved(event.amountMinor))
                else Transition.Ignored("Approved amount must be greater than zero")

            is ClaimEvent.Reject ->
                if (event.reason.isNotBlank()) Transition.Moved(ClaimState.Rejected(event.reason))
                else Transition.Ignored("Rejection requires a reason")

            else -> Transition.Ignored("Under review only Approve, Reject or RequestInfo are allowed")
        }
    }

    fun replay(events: List<ClaimEvent>): ClaimState =
        events.fold<ClaimEvent, ClaimState>(ClaimState.Draft) { state, event ->
            when (val t = reduce(state, event)) {
                is Transition.Moved -> t.to
                is Transition.Ignored -> state
            }
        }
}

