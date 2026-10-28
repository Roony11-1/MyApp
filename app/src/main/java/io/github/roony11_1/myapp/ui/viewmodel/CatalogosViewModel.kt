package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.domain.model.Categoria
import io.github.roony11_1.myapp.domain.model.Cuenta
import io.github.roony11_1.myapp.domain.model.Etiqueta
import io.github.roony11_1.myapp.domain.repository.ICategoriaRepository
import io.github.roony11_1.myapp.domain.repository.ICuentaRepository
import io.github.roony11_1.myapp.domain.repository.IEtiquetaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogosUiState(
    val cuentas: List<Cuenta> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val etiquetas: List<Etiqueta> = emptyList(),
    val cargando: Boolean = false,
    val procesando: Boolean = false,
    val error: String? = null)

class CatalogosViewModel(
    private val cuentaRepository: ICuentaRepository,
    private val categoriaRepository: ICategoriaRepository,
    private val etiquetaRepository: IEtiquetaRepository) : ViewModel()
{
    private val _uiState = MutableStateFlow(CatalogosUiState())
    val uiState: StateFlow<CatalogosUiState> = _uiState.asStateFlow()

    init
    {
        cargar()
    }

    fun cargar()
    {
        if (_uiState.value.cargando || _uiState.value.procesando)
            return

        _uiState.update {
            it.copy(cargando = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val cuentas = cuentaRepository.findAll()
                val categorias = categoriaRepository.findAll()
                val etiquetas = etiquetaRepository.findAll()

                _uiState.update {
                    it.copy(
                        cuentas = cuentas,
                        categorias = categorias,
                        etiquetas = etiquetas,
                        cargando = false,
                        error = null)
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(cargando = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = e.message ?: "No se pudieron cargar los catálogos")
                }
            }
        }
    }

    fun cambiarCuenta(id: Long, activa: Boolean) =
        cambiarEstado(
            activa = activa,
            operacion = { cuentaRepository.cambiarActiva(id, activa) },
            actualizar = { state, cambio ->
                state.copy(
                    cuentas = state.cuentas.map {
                        if (it.id == id) it.copy(activa = cambio) else it
                    })
            })

    fun agregarCategoria(nombre: String)
    {
        val limpio = nombre.trim()

        if (limpio.isEmpty() ||
            _uiState.value.procesando ||
            _uiState.value.cargando)
            return

        _uiState.update {
            it.copy(procesando = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val creada = categoriaRepository.crear(limpio)

                _uiState.update {
                    it.copy(
                        categorias = if (it.categorias.any { actual ->
                                actual.id == creada.id
                            })
                        {
                            it.categorias.map { actual ->
                                if (actual.id == creada.id) creada else actual
                            }
                        }
                        else
                        {
                            it.categorias + creada
                        },
                        procesando = false,
                        error = null)
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(procesando = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        procesando = false,
                        error = e.message ?: "No se pudo agregar la categoría")
                }
            }
        }
    }

    fun cambiarCategoria(id: Long, activa: Boolean) =
        cambiarEstado(
            activa = activa,
            operacion = { categoriaRepository.cambiarActiva(id, activa) },
            actualizar = { state, cambio ->
                state.copy(
                    categorias = state.categorias.map {
                        if (it.id == id) it.copy(activa = cambio) else it
                    })
            })

    fun cambiarEtiqueta(id: Long, activa: Boolean) =
        cambiarEstado(
            activa = activa,
            operacion = { etiquetaRepository.cambiarActiva(id, activa) },
            actualizar = { state, cambio ->
                state.copy(
                    etiquetas = state.etiquetas.map {
                        if (it.id == id) it.copy(activa = cambio) else it
                    })
            })

    private fun cambiarEstado(
        activa: Boolean,
        operacion: suspend () -> Boolean,
        actualizar: (CatalogosUiState, Boolean) -> CatalogosUiState)
    {
        if (_uiState.value.procesando)
            return

        _uiState.update {
            it.copy(procesando = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val actualizado = operacion()

                if (!actualizado)
                    throw IllegalStateException("No se pudo actualizar el elemento")

                _uiState.update {
                    actualizar(it, activa).copy(
                        procesando = false,
                        error = null)
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(procesando = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        procesando = false,
                        error = e.message ?: "No se pudo actualizar el elemento")
                }
            }
        }
    }

    companion object
    {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CatalogosViewModel(
                    cuentaRepository = container.cuentaRepository,
                    categoriaRepository = container.categoriaRepository,
                    etiquetaRepository = container.etiquetaRepository)
            }
        }
    }
}
