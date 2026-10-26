package io.github.roony11_1.myapp.ui.viewmodel

import io.github.roony11_1.myapp.core.pagination.Page

data class PagedData<T>(
    val items: List<T>,
    val page: Page<T>,
    val loadingMore: Boolean = false,
    val loadMoreError: String? = null)
{
    val hayMas: Boolean get() = page.hasNext
}

data class PagedUiState<T>(
    val items: List<T> = emptyList(),
    val page: Page<T>? = null,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val loadingMore: Boolean = false,
    val loadMoreError: String? = null) : IUiState<PagedData<T>>
{
    override val isIdle: Boolean
        get() = page == null && !isLoading && error == null

    override val data: PagedData<T>?
        get() = page?.let {
            PagedData(
                items = items,
                page = it,
                loadingMore = loadingMore,
                loadMoreError = loadMoreError)
        }
}
