# ex21 · OkHttp: `Interceptor` + `Authenticator` (token refresh)

**Difficulty:** hard · **Time:** 50 min · **Category:** Network · OkHttp and auth · **Type:** implement

## Goal
Add the access token to every request automatically, and refresh it only once after a `401`, even when 5 requests get `401` at the same time.

- **File:** `Auth.kt`
- **Run:** `./gradlew :ex21-okhttp-auth:test`

## Task
1. `AuthInterceptor`: adds `Authorization: Bearer <access>`. A request with the header `No-Auth: true` goes without a token, and the `No-Auth` header itself is removed.
2. `TokenAuthenticator.authenticate(route, response)`:
   - the request was already retried (`response.priorResponse != null`) → `null`,
   - the token in the request ≠ the current one in `TokenStore` → someone already refreshed: retry with the current token,
   - otherwise (under a lock) `refresh(refreshToken)`; success → save and retry; failure → `tokens.clear()`, `onLogout()`, `null`.
3. `buildClient(...)`: both in one `OkHttpClient`.

**Extra (optional):** a test that sends 5 requests in parallel and checks that `refresh` was called once.

## Background
- An `Interceptor` changes every request. An `Authenticator` is called only after a `401` and returns the request to retry (or `null` to give up).
- OkHttp runs these on its own threads, not in coroutines, so `refresh` is blocking and the lock is `synchronized`.
- Comparing the request's token with the current one *inside* the lock is what stops 5 parallel refreshes.
- `response.priorResponse != null` stops the endless loop 401 → refresh → 401.
