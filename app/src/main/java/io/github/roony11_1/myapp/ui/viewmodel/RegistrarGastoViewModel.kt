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
import io.github.roony11_1.myapp.domain.repository.ICuentaRepository
import io.github.roony11_1.myapp.domain.usecase.RegistrarGasto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrarGastoUiState(
    val categorias: List<Categoria> = emptyList(),
    val cuentas: List<Cuenta> = emptyList(),
    val cargandoOpciones: Boolean = false,
    val guardando: Boolean = false,
    val error: String? = null,
    val gastoGuardado: Gasto? = null)
{
    val ocupado: Boolean get() = cargandoOpciones || guardando
}

class RegistrarGastoViewModel(
    private val registrarGasto: RegistrarGasto,
    private val categoriaRepository: ICategoriaRepository,
    private val cuentaRepository: ICuentaRepository) : ViewModel()
{
    private val _uiState = MutableStateFlow(RegistrarGastoUiState())
    val uiState: StateFlow<RegistrarGastoUiState> = _uiState.asStateFlow()

    init
    {
        cargarOpciones()
    }

    private fun cargarOpciones()
    {
        _uiState.update {
            it.copy(cargandoOpciones = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val categorias = categoriaRepository.findAll()
                val cuentas = cuentaRepository.findAll()

                _uiState.update {
                    it.copy(
                        categorias = categorias,
                        cuentas = cuentas,
                        cargandoOpciones = false)
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(cargandoOpciones = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        cargandoOpciones = false,
                        error = e.message ?: "Error al cargar opciones")
                }
            }
        }
    }

    fun onGuardar(
        monto: Double,
        descripcion: String,
        cuenta: Cuenta,
        categorias: List<Categoria>)
    {
        if (_uiState.value.guardando)
            return

        _uiState.update {
            it.copy(guardando = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val gasto = registrarGasto(
                    monto,
                    descripcion,
                    cuenta,
                    categorias)

                _uiState.update {
                    it.copy(guardando = false, gastoGuardado = gasto)
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(guardando = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        error = e.message ?: "Error al guardar")
                }
            }
        }
    }

    fun onExitoConsumido()
    {
        _uiState.update { it.copy(gastoGuardado = null) }
    }

    companion object
    {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegistrarGastoViewModel(
                    registrarGasto = RegistrarGasto(
                        gastoRepository = container.gastoRepository,
                        clock = container.clock),
                    categoriaRepository = container.categoriaRepository,
                    cuentaRepository = container.cuentaRepository)
            }
        }
    }
}
