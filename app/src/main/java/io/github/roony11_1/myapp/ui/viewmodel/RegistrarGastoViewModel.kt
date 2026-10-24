package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.usecase.RegistrarGasto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrarGastoViewModel(
    private val registrarGasto: RegistrarGasto): ViewModel()
{
    private val _uiState = MutableStateFlow<UiState<Gasto>>(UiState.Idle)

    val uiState: StateFlow<UiState<Gasto>> = _uiState.asStateFlow()

    fun onGuardar(
        monto: Double,
        descripcion: String,
        cuentaId: Long,
        categoriaId: Long)
    {
        viewModelScope.launch {
            _uiState.value = UiState.Cargando

            try
            {
                val gasto = registrarGasto(monto, descripcion, cuentaId, categoriaId)

                _uiState.value = UiState.Exito(gasto)
            }
            catch (e: Exception)
            {
                _uiState.value = UiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    companion object
    {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegistrarGastoViewModel(
                    registrarGasto = RegistrarGasto(
                        gastoRepository = container.gastoRepository,
                        clock = container.clock))
            }
        }
    }
}