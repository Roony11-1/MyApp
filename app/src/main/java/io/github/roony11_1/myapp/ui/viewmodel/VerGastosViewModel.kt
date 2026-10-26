package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.usecase.VerGastos
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerGastosUiState(
    val gastos: List<Gasto> = emptyList(),
    val pagina: Page<Gasto>? = null,
    val cargando: Boolean = false,
    val error: String? = null)
{
    val cargandoInicial: Boolean get() = cargando && gastos.isEmpty()

    val cargandoMas: Boolean get() = cargando && gastos.isNotEmpty()

    val hayMas: Boolean get() = pagina?.hasNext ?: true

    val sinResultados: Boolean
        get() = !cargando && error == null && pagina != null &&
                gastos.isEmpty()
}

class VerGastosViewModel(
    private val verGastos: VerGastos,
    private val pageSize: Int = PAGE_SIZE) : ViewModel()
{
    private val _uiState = MutableStateFlow(VerGastosUiState())
    val uiState: StateFlow<VerGastosUiState> = _uiState.asStateFlow()

    fun cargar()
    {
        val state = _uiState.value
        if (state.cargando || state.pagina != null)
            return

        cargarPagina(numero = 0, reemplazar = true)
    }

    fun cargarMas()
    {
        val state = _uiState.value
        val pagina = state.pagina
        if (state.cargando || pagina == null || !pagina.hasNext)
            return

        cargarPagina(numero = pagina.number + 1, reemplazar = false)
    }

    private fun cargarPagina(numero: Int, reemplazar: Boolean)
    {
        _uiState.update {
            if (reemplazar)
                it.copy(
                    gastos = emptyList(),
                    pagina = null,
                    cargando = true,
                    error = null)
            else
                it.copy(cargando = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val page = verGastos(
                    number = numero,
                    size = pageSize)

                _uiState.update {
                    it.copy(
                        gastos = if (reemplazar) page.content
                                 else it.gastos + page.content,
                        pagina = page,
                        cargando = false)
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
                        error = e.message ?: if (reemplazar)
                            "Error al cargar" else "Error al cargar más")
                }
            }
        }
    }

    companion object
    {
        private const val PAGE_SIZE = 20

        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                VerGastosViewModel(
                    verGastos = VerGastos(container.gastoRepository))
            }
        }
    }
}
