package io.github.roony11_1.myapp.core.pagination

data class Page<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int)
{
    val first: Boolean get() = number == 0

    val last: Boolean get() = number >= totalPages - 1

    val empty: Boolean get() = content.isEmpty()

    val hasNext: Boolean get() = !last
}