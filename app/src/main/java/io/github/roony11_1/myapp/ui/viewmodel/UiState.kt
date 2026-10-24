package io.github.roony11_1.myapp.ui.viewmodel

sealed interface UiState<out T>
{
    data object Idle : UiState<Nothing>
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val data: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}