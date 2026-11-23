# ex23 · Paging 3: PagingSource

**Difficulty:** medium · **Time:** 40 min · **Category:** Android · Paging 3 · **Type:** implement

## Goal
Write the `PagingSource` for a long list of pharmacies from an API with page numbers. You test the `PagingSource` without UI, using `TestPager`.

- **File:** `Pharmacies.kt`
- **Run:** `./gradlew :ex23-paging3:test`
- **Small steps:** `StepsTest.kt` tests single building blocks, start there: `./gradlew :ex23-paging3:test --tests '*StepsTest'`

## Task
1. `load(params)`:
   - the first page is number 1 (`params.key` is then `null`),
   - take the page size from `params.loadSize`,
   - `prevKey` = the previous page or `null` on the first one, `nextKey` = the next page or `null` on the last one,
   - `IOException` → `LoadResult.Error`.
2. `getRefreshKey(state)`: the page number around the place the user was looking at (`anchorPosition`).

**Extra (optional):** expose `Flow<PagingData<Pharmacy>>` with `Pager(...)`, and describe where you would put `cachedIn(viewModelScope)` and why.

## Background
- `PagingSource` only loads pages. `Pager`/`PagingData` cache and join them, and the UI asks for more when it scrolls.
- `nextKey = null` tells Paging the list has ended; `prevKey = null` that it has no beginning before this page.
- `getRefreshKey` decides which page to start from after a refresh (for example after `invalidate()`).
- Errors go back as `LoadResult.Error`, so the UI can show "retry" at the bottom of the list.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
// Inside load(params: LoadParams<Int>):
params.key                  // Int? – null on the first load
params.loadSize             // how many items Paging asks for

LoadResult.Page(data = items, prevKey = null, nextKey = 2)
LoadResult.Error<Int, Item>(exception)

// Inside getRefreshKey(state: PagingState<Int, Item>):
state.anchorPosition                          // Int? – the position the user last saw
state.closestPageToPosition(position)         // LoadResult.Page? with prevKey / nextKey
key?.plus(1)

// Test
val pager = TestPager(PagingConfig(pageSize = 20), source)
val first = pager.refresh() as PagingSource.LoadResult.Page
val second = pager.append() as PagingSource.LoadResult.Page
```

- [Android: load and display paged data](https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data)
- [Android: test your Paging implementation](https://developer.android.com/topic/libraries/architecture/paging/test)
