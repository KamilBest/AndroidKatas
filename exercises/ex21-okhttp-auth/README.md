# ex21 · OkHttp: `Interceptor` + `Authenticator` (token refresh)

**Difficulty:** hard · **Time:** 50 min · **Category:** Network · OkHttp and auth · **Type:** implement

## Goal
Add the access token to every request automatically, and refresh it only once after a `401`, even when 5 requests get `401` at the same time.

Most APIs need an access token in every request. Tokens expire: the server answers `401`, the app gets a new token using a refresh token, and repeats the request. OkHttp has two places for this: an `Interceptor` runs for every request and adds the header, and an `Authenticator` runs only after a `401`.

The hard case: 5 requests get `401` at the same time. A naive version refreshes 5 times, and the server may invalidate the earlier tokens. You write both parts so that only one refresh happens and the other 4 requests retry with the new token.

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

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
class ApiKeyInterceptor(private val keys: KeyStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("X-Api-Key", keys.current())
            .removeHeader("X-Debug")
            .build()
        return chain.proceed(request)
    }
}

class RetryAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.priorResponse != null) return null      // already retried once
        val sentHeader: String? = response.request.header("Authorization")
        synchronized(this) {
            // compare, maybe refresh, then retry:
            return response.request.newBuilder()
                .header("Authorization", "Bearer new-token")
                .build()
        }
    }
}

val client = OkHttpClient.Builder()
    .addInterceptor(ApiKeyInterceptor(keys))
    .authenticator(RetryAuthenticator())
    .build()
```

- [OkHttp (docs: Interceptors, Recipes → Handling authentication)](https://github.com/square/okhttp)
- [Android: security best practices](https://developer.android.com/privacy-and-security/security-tips)
