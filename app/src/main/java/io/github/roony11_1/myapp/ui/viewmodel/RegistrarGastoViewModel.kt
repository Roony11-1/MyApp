package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.domain.model.Categoria
import io.github.roony11_1.myapp.domain.model.Cuenta
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.ICategoriaRepository
import io.github.roony11_1.myapp.domain.usecase.RegistrarGasto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrarGastoViewModel(
    private val registrarGasto: RegistrarGasto,
    private val categoriaRepository: ICategoriaRepository): ViewModel()
{
    private val _uiState = MutableStateFlow<UiState<Gasto>>(UiState.Idle)
    val uiState: StateFlow<UiState<Gasto>> = _uiState.asStateFlow()

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias = _categorias.asStateFlow()

    init
    {
        viewModelScope.launch {
            try
            {
                _categorias.value = categoriaRepository.findAll()
            }
            catch (e: CancellationException)
            {
                throw e
            }
            catch (e: Exception)
            {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar categorías")
            }
        }
    }

    val cuentas = listOf<Cuenta>(Cuenta(1L, "Cuenta por defecto"))

    fun onGuardar(
        monto: Double,
        descripcion: String,
        cuenta: Cuenta,
        categorias: List<Categoria>)
    {
        viewModelScope.launch {
            _uiState.value = UiState.Cargando

            try
            {
                val gasto = registrarGasto(monto, descripcion, cuenta, categorias)

                _uiState.value = UiState.Exito(gasto)
            }
            catch (e: Exception)
            {
                _uiState.value = UiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun onExitoConsumido() {
        _uiState.value = UiState.Idle
    }

    companion object
    {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegistrarGastoViewModel(
                    registrarGasto = RegistrarGasto(
                        gastoRepository = container.gastoRepository,
                        clock = container.clock),
                    container.categoriaRepository)
            }
        }
    }
}