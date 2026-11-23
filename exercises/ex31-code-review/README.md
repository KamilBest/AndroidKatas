# ex31 · Code review: find the problems in a PR

**Difficulty:** hard · **Time:** 45 min · **Category:** Practice · code review · **Type:** review

## Goal
Review a colleague's pull request: find the problems, rank them and explain each one.

- **Read:** `PR_DESCRIPTION.md` and the 4 files in `src/main/kotlin/katas/ex31/`
- **Output:** `REVIEW.md` in the module folder

## Task
1. Read the PR description and the code (it compiles and "works", but has more than ten problems).
2. In `REVIEW.md` write each problem as: `- [blocker|major|minor|nit] File.kt:line – what is wrong → why → how to fix`.
3. Sort from the most serious.
4. Optionally fix the code on a separate branch.

Tone: specific, with reasons, no sarcasm.

**Extra (optional):** a 45-minute limit, and one decision sentence at the top of `REVIEW.md`: approve / request changes, and why.

## Background
- Check in this order: security and privacy → memory leaks → coroutines → UI state → Compose → error handling → style.
- Priorities: `blocker` (data leak, crash, data loss), `major` (memory leak, wrong thread), `minor` (readability, missing test), `nit` (cosmetics).
- Health data and tokens are sensitive: no personal data in logs, no tokens in plain `SharedPreferences`.
- Write about the code, not the person ("this line logs the PESEL"), and mention what is good.

## Reference
No code to write here: you read and judge.

- [Google: how to do a code review](https://google.github.io/eng-practices/review/reviewer/)
- [Android: security best practices](https://developer.android.com/privacy-and-security/security-tips)
- [Android: Compose side-effects](https://developer.android.com/develop/ui/compose/side-effects)
