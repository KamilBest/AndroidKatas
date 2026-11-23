package katas.ex21

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryTokenStore(initial: Tokens?) : TokenStore {
    @Volatile private var tokens = initial
    override fun current() = tokens
    override fun save(tokens: Tokens) { this.tokens = tokens }
    override fun clear() { tokens = null }
}

class AuthTest {
    private val server = MockWebServer()
    private val store = InMemoryTokenStore(Tokens("old", "r1"))
    private val refreshCalls = AtomicInteger()
    private var refreshResult: Tokens? = Tokens("new", "r2")
    private var loggedOut = false
    private lateinit var client: OkHttpClient

    @Before fun setUp() {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when {
                request.path == "/login" -> MockResponse().setBody("login-ok")
                request.getHeader("Authorization") == "Bearer new" -> MockResponse().setBody("ok")
                else -> MockResponse().setResponseCode(401)
            }
        }
        server.start()
        client = buildClient(
            store,
            refresh = { refreshToken ->
                refreshCalls.incrementAndGet()
                Thread.sleep(200) // a slow endpoint: gives races a chance
                if (refreshToken == "r1") refreshResult else null
            },
            onLogout = { loggedOut = true },
        )
    }

    @After fun tearDown() = server.shutdown()

    private fun get(path: String, noAuth: Boolean = false) = client.newCall(
        Request.Builder().url(server.url(path)).apply { if (noAuth) header("No-Auth", "true") }.build(),
    ).execute().use { it.code to it.body!!.string() }

    @Test fun `adds bearer token`() {
        store.save(Tokens("new", "r1"))
        assertEquals(200 to "ok", get("/claims"))
        assertEquals("Bearer new", server.takeRequest().getHeader("Authorization"))
    }

    @Test fun `no-auth requests go without token and without marker header`() {
        assertEquals(200 to "login-ok", get("/login", noAuth = true))
        val request = server.takeRequest()
        assertNull(request.getHeader("Authorization"))
        assertNull(request.getHeader("No-Auth"))
    }

    @Test fun `401 refreshes once and retries`() {
        assertEquals(200 to "ok", get("/claims"))
        assertEquals(1, refreshCalls.get())
        assertEquals(Tokens("new", "r2"), store.current())
        assertEquals("Bearer old", server.takeRequest().getHeader("Authorization"))
        assertEquals("Bearer new", server.takeRequest().getHeader("Authorization"))
    }

    @Test fun `parallel 401s trigger a single refresh`() {
        val pool = Executors.newFixedThreadPool(5)
        val start = CountDownLatch(1)
        val results = (1..5).map { pool.submit<Pair<Int, String>> { start.await(); get("/claims/$it") } }
        start.countDown()
        results.forEach { assertEquals(200 to "ok", it.get(10, TimeUnit.SECONDS)) }
        pool.shutdown()
        assertEquals(1, refreshCalls.get())
    }

    @Test fun `failed refresh logs out and returns 401 without looping`() {
        refreshResult = null
        assertEquals(401, get("/claims").first)
        assertTrue(loggedOut)
        assertNull(store.current())
        assertEquals(1, refreshCalls.get())
        assertEquals(1, server.requestCount)
    }

    @Test fun `still 401 after refresh does not loop`() {
        refreshResult = Tokens("still-bad", "r2")
        assertEquals(401, get("/claims").first)
        assertEquals(2, server.requestCount)
        assertEquals(1, refreshCalls.get())
    }
}
