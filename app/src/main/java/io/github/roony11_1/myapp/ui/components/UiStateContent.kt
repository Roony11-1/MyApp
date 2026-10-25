package io.github.roony11_1.myapp.ui.components

import androidx.compose.runtime.Composable
import io.github.roony11_1.myapp.ui.viewmodel.UiState

@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    content: @Composable (
        isLoading: Boolean,
        error: String?,
        data: T?) -> Unit)
{
    content(
        state is UiState.Cargando,
        (state as? UiState.Error)?.mensaje,
        (state as? UiState.Exito<T>)?.data)
}