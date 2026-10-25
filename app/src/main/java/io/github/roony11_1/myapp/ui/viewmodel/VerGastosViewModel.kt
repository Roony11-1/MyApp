package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.usecase.VerGastos
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerGastosViewModel(
    private val verGastos: VerGastos) : ViewModel()
{
    private val pageSize = 1
    private val _uiState = MutableStateFlow<UiState<List<Gasto>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Gasto>>> = _uiState.asStateFlow()

    private val _cargandoMas = MutableStateFlow(false)
    val cargandoMas = _cargandoMas.asStateFlow()

    private val _hayMas = MutableStateFlow(true)
    val hayMas = _hayMas.asStateFlow()

    private val _errorAlCargarMas = MutableStateFlow<String?>(null)
    val errorAlCargarMas = _errorAlCargarMas.asStateFlow()

    fun cargar() {
        if (_uiState.value is UiState.Cargando || _cargandoMas.value) return

        _uiState.value = UiState.Cargando
        _hayMas.value = true
        _errorAlCargarMas.value = null

        viewModelScope.launch {
            try
            {
                val gastos = verGastos(limit = pageSize, offset = 0)

                _hayMas.value = gastos.size == pageSize
                _uiState.value = UiState.Exito(gastos)
            }
            catch (e: CancellationException)
            {
                _uiState.value = UiState.Idle
                throw e
            }
            catch (e: Exception)
            {
                _uiState.value = UiState.Error(
                    e.message ?: "Error al cargar"
                )
            }
        }
    }

    fun cargarMas()
    {
        val state = _uiState.value
        if (state !is UiState.Exito) return
        if (_cargandoMas.value || !_hayMas.value) return

        _cargandoMas.value = true
        _errorAlCargarMas.value = null

        viewModelScope.launch {
            try
            {
                val nuevos = verGastos(
                    limit = pageSize,
                    offset = state.data.size)

                _hayMas.value = nuevos.size == pageSize
                _uiState.value = UiState.Exito(state.data + nuevos)
            }
            catch (e: CancellationException) {
                throw e
            }
            catch (e: Exception)
            {
                _errorAlCargarMas.value =
                    e.message ?: "Error al cargar más"
            }
            finally
            {
                _cargandoMas.value = false
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