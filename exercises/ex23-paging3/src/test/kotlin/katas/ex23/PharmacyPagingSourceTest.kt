package katas.ex23

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

// GIVEN: do not change
class FakePharmacyListApi(private val all: List<Pharmacy>) : PharmacyListApi {
    val calls = mutableListOf<Triple<String, Int, Int>>()
    var error: Exception? = null

    override suspend fun page(city: String, page: Int, size: Int): PharmacyPage {
        calls += Triple(city, page, size)
        error?.let { throw it }
        val chunks = all.chunked(size)
        return PharmacyPage(chunks.getOrElse(page - 1) { emptyList() }, page, chunks.size)
    }
}

class PharmacyPagingSourceTest {
    private val pharmacies = (1..5).map { Pharmacy("p$it", "Pharmacy $it") }
    private val api = FakePharmacyListApi(pharmacies)
    private val config = PagingConfig(pageSize = 2, initialLoadSize = 2, enablePlaceholders = false)
    private val pager = TestPager(config, PharmacyPagingSource(api, "Warszawa"))

    @Test fun `first page starts at 1 and has no previous key`() = runTest {
        val page = assertIs<LoadResult.Page<Int, Pharmacy>>(pager.refresh())
        assertEquals(pharmacies.take(2), page.data)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
        assertEquals(Triple("Warszawa", 1, 2), api.calls.single())
    }

    @Test fun `appends until the last page`() = runTest {
        pager.refresh()
        val second = assertIs<LoadResult.Page<Int, Pharmacy>>(pager.append())
        assertEquals(1, second.prevKey)
        assertEquals(3, second.nextKey)
        val last = assertIs<LoadResult.Page<Int, Pharmacy>>(pager.append())
        assertEquals(listOf(pharmacies[4]), last.data)
        assertNull(last.nextKey, "page 3 of 3 is the end")
        assertNull(pager.append(), "no more pages to load")
    }

    @Test fun `network error becomes LoadResult Error`() = runTest {
        api.error = IOException("offline")
        val result = pager.refresh()
        assertIs<LoadResult.Error<Int, Pharmacy>>(result)
        assertIs<IOException>(result.throwable)
    }

    @Test fun `refresh key points at the page around the anchor`() = runTest {
        pager.refresh()
        pager.append()
        val state = pager.getPagingState(anchorPosition = 3) // p4, so page 2
        assertEquals(2, PharmacyPagingSource(api, "Warszawa").getRefreshKey(state))
    }
}
