package katas.ex21

import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

// GIVEN
data class Tokens(val access: String, val refresh: String)

interface TokenStore {
    fun current(): Tokens?
    fun save(tokens: Tokens)
    fun clear()
}

class AuthInterceptor(private val tokens: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = TODO()
}

class TokenAuthenticator(
    private val tokens: TokenStore,
    private val refresh: (refreshToken: String) -> Tokens?, // a blocking call to the /auth/refresh endpoint
    private val onLogout: () -> Unit,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? = TODO()
}

fun buildClient(tokens: TokenStore, refresh: (String) -> Tokens?, onLogout: () -> Unit): OkHttpClient = TODO()
