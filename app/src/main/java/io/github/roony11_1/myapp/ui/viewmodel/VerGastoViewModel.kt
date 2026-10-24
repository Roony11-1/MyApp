package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.usecase.VerGasto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerGastoViewModel(
    private val verGasto: VerGasto,
    private val gastoId: Long) : ViewModel()
{
    private val _uiState = MutableStateFlow<UiState<Gasto?>>(UiState.Idle)
    val uiState: StateFlow<UiState<Gasto?>> = _uiState.asStateFlow()

    fun cargar()
    {
        viewModelScope.launch {
            _uiState.value = UiState.Cargando

            try
            {
                val gasto = verGasto(gastoId)

                _uiState.value = UiState.Exito(gasto)
            }
            catch (e: Exception)
            {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar")
            }
        }
    }

    companion object
    {
        fun factory(container: AppContainer, gastoId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                VerGastoViewModel(
                    verGasto = VerGasto(container.gastoRepository), gastoId)
            }
        }
    }
}