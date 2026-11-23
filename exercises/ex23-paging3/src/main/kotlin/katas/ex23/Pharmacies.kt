package katas.ex23

import androidx.paging.PagingSource
import androidx.paging.PagingState

// GIVEN: an API paged by page number (from 1)
data class Pharmacy(val id: String, val name: String)
data class PharmacyPage(val items: List<Pharmacy>, val page: Int, val totalPages: Int)

interface PharmacyListApi {
    /** Throws IOException when there is no network. */
    suspend fun page(city: String, page: Int, size: Int): PharmacyPage
}

class PharmacyPagingSource(
    private val api: PharmacyListApi,
    private val city: String,
) : PagingSource<Int, Pharmacy>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pharmacy> = TODO()

    override fun getRefreshKey(state: PagingState<Int, Pharmacy>): Int? = TODO()
}

/** Step 1: the previous page key (pages start at 1). */
fun prevKeyFor(page: Int): Int? = TODO()

/** Step 2: the next page key; `null` when this is the last page or it came back empty. */
fun nextKeyFor(page: Int, totalPages: Int, itemCount: Int): Int? = TODO()
