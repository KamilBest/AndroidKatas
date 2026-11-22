package katas.ex01

import katas.ex01.ClaimEvent.*
import katas.ex01.ClaimState.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ClaimMachineTest {

    private fun movesTo(from: ClaimState, event: ClaimEvent, expected: ClaimState) =
        assertEquals(Transition.Moved(expected), ClaimMachine.reduce(from, event), "$from + $event")

    private fun ignores(from: ClaimState, event: ClaimEvent) =
        assertIs<Transition.Ignored>(ClaimMachine.reduce(from, event), "$from + $event should be ignored")

    @Test fun `happy path`() {
        movesTo(Draft, Submit, Submitted)
        movesTo(Submitted, AssignReviewer("ania"), UnderReview("ania"))
        movesTo(UnderReview("ania"), Approve(15_000), Approved(15_000))
        movesTo(Approved(15_000), Pay, Paid)
    }

    @Test fun `info loop keeps reviewer`() {
        movesTo(UnderReview("ania"), RequestInfo("Invoice scan?"), NeedsInfo("ania", "Invoice scan?"))
        movesTo(NeedsInfo("ania", "Invoice scan?"), ProvideInfo("attached"), UnderReview("ania"))
    }

    @Test fun `rejection with reason`() {
        movesTo(UnderReview("ania"), Reject("No policy"), Rejected("No policy"))
    }

    @Test fun `validation of approve and reject`() {
        ignores(UnderReview("ania"), Approve(0))
        ignores(UnderReview("ania"), Approve(-100))
        ignores(UnderReview("ania"), Reject("   "))
    }

    @Test fun `illegal transitions are ignored`() {
        ignores(Draft, Pay)
        ignores(Draft, Approve(100))
        ignores(Submitted, Submit)
        ignores(Submitted, Approve(100))
        ignores(NeedsInfo("a", "q"), Approve(100))
        ignores(Approved(100), Reject("too late"))
        ignores(UnderReview("ania"), Pay)
    }

    @Test fun `terminal states ignore everything`() {
        val all = listOf(Submit, AssignReviewer("x"), RequestInfo("q"), ProvideInfo("a"), Approve(1), Reject("r"), Pay)
        for (terminal in listOf(Rejected("r"), Paid)) {
            assertTrue(terminal.isTerminal)
            all.forEach { ignores(terminal, it) }
        }
        listOf(Draft, Submitted, UnderReview("a"), NeedsInfo("a", "q"), Approved(1)).forEach { assertFalse(it.isTerminal, "$it") }
    }

    @Test fun `replay folds events and skips ignored`() {
        val events = listOf(Pay, Submit, Submit, AssignReviewer("ola"), RequestInfo("?"), Approve(5), ProvideInfo("!"), Approve(500), Pay, Reject("x"))
        assertEquals(Paid, ClaimMachine.replay(events))
        assertEquals(Draft, ClaimMachine.replay(emptyList()))
    }
}
