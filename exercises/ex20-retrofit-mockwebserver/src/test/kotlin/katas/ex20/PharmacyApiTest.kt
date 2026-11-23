package katas.ex20

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PharmacyApiTest {
    private val server = MockWebServer()
    private lateinit var dataSource: PharmacyRemoteDataSource

    @Before fun setUp() {
        server.start()
        val client = OkHttpClient.Builder().readTimeout(1, TimeUnit.SECONDS).build()
        dataSource = PharmacyRemoteDataSource(createPharmacyApi(server.url("/v1/"), client))
    }

    @After fun tearDown() = server.shutdown()

    private fun pharmacyJson(id: String, extra: String = "") =
        """{"id":"$id","name":"Pharmacy $id","address":{"street":"Prosta 1","city":"Warszawa"},"opening_hours":"08:00-20:00"$extra}"""

    private fun page(n: Int, total: Int, vararg ids: String) =
        MockResponse().setBody("""{"items":[${ids.joinToString(",") { pharmacyJson(it) }}],"page":$n,"total_pages":$total}""")

    @Test fun `parses pharmacy with defaults and unknown fields`() = runTest {
        server.enqueue(MockResponse().setBody(pharmacyJson("p1", ""","is_24h":true,"rating":4.5,"brand_new_field":{"x":1}""")))
        server.enqueue(MockResponse().setBody(pharmacyJson("p2")))
        assertEquals(
            PharmacyDto("p1", "Pharmacy p1", AddressDto("Prosta 1", "Warszawa"), "08:00-20:00", isOpen24h = true, rating = 4.5),
            dataSource.findPharmacy("p1"),
        )
        val p2 = dataSource.findPharmacy("p2")!!
        assertEquals(false, p2.isOpen24h)
        assertNull(p2.rating)
        assertEquals("/v1/pharmacies/p1", server.takeRequest().path)
    }

    @Test fun `404 means not found, other errors propagate`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))
        assertNull(dataSource.findPharmacy("nope"))
        server.enqueue(MockResponse().setResponseCode(500))
        val error = assertFailsWith<HttpException> { dataSource.findPharmacy("p1") }
        assertEquals(500, error.code())
    }

    @Test fun `sends city and page as encoded query params`() = runTest {
        server.enqueue(page(1, 3, "a", "b"))
        assertEquals(listOf("a", "b"), dataSource.pharmaciesIn("Zielona Góra").map { it.id })
        assertEquals("/v1/pharmacies?city=Zielona%20G%C3%B3ra&page=1", server.takeRequest().path)
    }

    @Test fun `reserve sends json body and maps 201`() = runTest {
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"pickup_code":"7Q2K"}"""))
        assertEquals(ReserveResult.Reserved("7Q2K"), dataSource.reserve("rx-9", "p1", listOf("rx-1", "rx-2")))
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/v1/prescriptions/rx-9/reserve", request.path)
        assertEquals("""{"pharmacy_id":"p1","items":["rx-1","rx-2"]}""", request.body.readUtf8())
        assertEquals(true, request.getHeader("Content-Type")?.startsWith("application/json"))
    }

    @Test fun `reserve maps error codes`() = runTest {
        server.enqueue(MockResponse().setResponseCode(409))
        server.enqueue(MockResponse().setResponseCode(404))
        server.enqueue(MockResponse().setResponseCode(503))
        assertEquals(ReserveResult.AlreadyReserved, dataSource.reserve("rx", "p1", listOf("a")))
        assertEquals(ReserveResult.NotFound, dataSource.reserve("rx", "p1", listOf("a")))
        assertEquals(ReserveResult.Failed(503), dataSource.reserve("rx", "p1", listOf("a")))
    }

    @Test fun `timeout is reported as offline`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
        assertEquals(ReserveResult.Offline, dataSource.reserve("rx", "p1", listOf("a")))
    }
}
