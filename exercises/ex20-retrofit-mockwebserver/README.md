# ex20 · Retrofit + kotlinx.serialization + MockWebServer

**Difficulty:** medium · **Time:** 45 min · **Category:** Network · Retrofit · **Type:** implement

## Goal
Write the network layer of a "Pharmacies" feature. The tests run real Retrofit against a local HTTP server and check *what you send* (path, query, body) and *how you parse* the response.

- **Files:** `Dtos.kt`, `PharmacyApi.kt`
- **Run:** `./gradlew :ex20-retrofit-mockwebserver:test`

## Task
Backend API:
```
GET  /v1/pharmacies?city=Warszawa&page=1   → {"items":[…],"page":1,"total_pages":3}
GET  /v1/pharmacies/{id}                   → PharmacyDto | 404
POST /v1/prescriptions/{id}/reserve        body {"pharmacy_id":"p1","items":["rx-1"]}
                                           → 201 {"pickup_code":"7Q2K"} | 409 | 404
```
A pharmacy in JSON has `opening_hours`, optional `is_24h` and `rating`; the backend adds new fields without warning.

1. `Dtos.kt`: serialization annotations (snake_case, default values for optional fields).
2. `PharmacyApi`: Retrofit annotations. `reserve` returns `Response<…>`, the rest return the body directly.
3. `createPharmacyApi`: Retrofit + `Json { ignoreUnknownKeys = true }`.
4. `PharmacyRemoteDataSource`:
   - `pharmaciesIn(city)`: the first page,
   - `findPharmacy(id)`: `null` for 404, other errors are rethrown,
   - `reserve(…)`: 201 → `Reserved`, 409 → `AlreadyReserved`, 404 → `NotFound`, other → `Failed(code)`, `IOException` → `Offline`.

**Extra (optional):** add `allPharmacies(city)` that loads all pages, with a test for three pages.

## Background
- A `suspend` function that returns the body throws `HttpException` on 4xx/5xx. `Response<T>` does not throw; you read `code()` yourself.
- `ignoreUnknownKeys` protects you from new backend fields.
- Paths in annotations have no leading `/`, because `baseUrl` ends with `/v1/`.
- `MockWebServer` lets a test queue responses (`enqueue`) and inspect requests (`takeRequest()`).

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
@Serializable
data class UserDto(
    val id: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("is_admin") val isAdmin: Boolean = false,   // optional field: give a default
)

interface UserApi {
    @GET("users")                                           // no leading "/"
    suspend fun users(@Query("page") page: Int): UsersPageDto

    @GET("users/{id}")
    suspend fun user(@Path("id") id: String): UserDto       // throws HttpException on 4xx/5xx

    @POST("users/{id}/block")
    suspend fun block(@Path("id") id: String, @Body body: BlockRequest): Response<BlockDto>
}

val json = Json { ignoreUnknownKeys = true }
val api: UserApi = Retrofit.Builder()
    .baseUrl("https://example.com/v1/")
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()
    .create(UserApi::class.java)

response.code()        // 201
response.body()        // BlockDto?
(e as HttpException).code()

// MockWebServer
server.enqueue(MockResponse().setResponseCode(200).setBody("""{"id":"1"}"""))
val request = server.takeRequest()
request.path                 // "/v1/users/1"
request.body.readUtf8()      // the JSON you sent
```

- [Retrofit (README and docs)](https://github.com/square/retrofit)
- [Kotlin: serialization](https://kotlinlang.org/docs/serialization.html)
- [OkHttp MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
