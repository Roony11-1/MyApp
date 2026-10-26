package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.usecase.VerGasto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface VerGastoUiState
{
    data object Inicial : VerGastoUiState
    data object Cargando : VerGastoUiState
    data class Error(val mensaje: String) : VerGastoUiState
    data class Exito(val gasto: Gasto?) : VerGastoUiState
}

class VerGastoViewModel(
    private val verGasto: VerGasto,
    private val gastoId: Long) : ViewModel()
{
    private val _uiState =
        MutableStateFlow<VerGastoUiState>(VerGastoUiState.Inicial)
    val uiState: StateFlow<VerGastoUiState> = _uiState.asStateFlow()

    fun cargar()
    {
        val state = _uiState.value
        if (state is VerGastoUiState.Cargando ||
            state is VerGastoUiState.Exito)
            return

        _uiState.value = VerGastoUiState.Cargando

        viewModelScope.launch {
            try
            {
                _uiState.value = VerGastoUiState.Exito(verGasto(gastoId))
            }
            catch (e: CancellationException)
            {
                _uiState.value = VerGastoUiState.Inicial
                throw e
            }
            catch (e: Exception)
            {
                _uiState.value = VerGastoUiState.Error(
                    e.message ?: "Error al cargar")
            }
        }
    }

    companion object
    {
        fun factory(
            container: AppContainer,
            gastoId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                VerGastoViewModel(
                    verGasto = VerGasto(container.gastoRepository), gastoId)
            }
        }
    }
}
