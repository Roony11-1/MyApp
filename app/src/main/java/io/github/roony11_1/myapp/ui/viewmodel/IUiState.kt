package io.github.roony11_1.myapp.ui.viewmodel

interface IUiState<out T>
{
    val isIdle: Boolean
    val isLoading: Boolean
    val error: String?
    val data: T?

    val isError: Boolean get() = error != null
}
