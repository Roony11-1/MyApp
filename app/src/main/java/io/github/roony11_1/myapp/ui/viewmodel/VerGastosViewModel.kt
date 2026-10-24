package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.usecase.VerGastos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerGastosViewModel(
    private val verGastos: VerGastos) : ViewModel()
{
    private val _uiState = MutableStateFlow<UiState<List<Gasto>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Gasto>>> = _uiState.asStateFlow()

    fun cargar()
    {
        viewModelScope.launch {
            _uiState.value = UiState.Cargando
            try
            {
                val gastos = verGastos()
                _uiState.value = UiState.Exito(gastos)
            }
            catch (e: Exception)
            {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar")
            }
        }
    }

    companion object
    {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                VerGastosViewModel(
                    verGastos = VerGastos(container.gastoRepository))
            }
        }
    }
}