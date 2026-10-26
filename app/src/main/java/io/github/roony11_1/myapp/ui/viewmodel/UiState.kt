package io.github.roony11_1.myapp.ui.viewmodel

sealed interface UiState<out T> : IUiState<T>
{
    data object Idle : UiState<Nothing>
    {
        override val isIdle: Boolean = true
        override val isLoading: Boolean = false
        override val error: String? = null
        override val data: Nothing? = null
    }

    data object Cargando : UiState<Nothing>
    {
        override val isIdle: Boolean = false
        override val isLoading: Boolean = true
        override val error: String? = null
        override val data: Nothing? = null
    }

    data class Exito<T>(override val data: T) : UiState<T>
    {
        override val isIdle: Boolean = false
        override val isLoading: Boolean = false
        override val error: String? = null
    }

    data class Error(val mensaje: String) : UiState<Nothing>
    {
        override val isIdle: Boolean = false
        override val isLoading: Boolean = false
        override val error: String = mensaje
        override val data: Nothing? = null
    }
}
