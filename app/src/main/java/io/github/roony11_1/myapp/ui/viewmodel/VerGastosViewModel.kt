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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerGastosViewModel(
    private val verGastos: VerGastos,
    private val pageSize: Int = PAGE_SIZE) : ViewModel()
{
    private val _uiState = MutableStateFlow(PagedUiState<Gasto>())
    val uiState: StateFlow<PagedUiState<Gasto>> = _uiState.asStateFlow()

    fun cargar()
    {
        val state = _uiState.value
        if (state.isLoading || state.loadingMore || state.page != null)
            return

        cargarPagina(numero = 0, reemplazar = true)
    }

    fun recargar()
    {
        val state = _uiState.value
        if (state.isLoading || state.loadingMore)
            return

        cargarPagina(numero = 0, reemplazar = true)
    }

    fun cargarMas()
    {
        val state = _uiState.value
        val pagina = state.page
        if (state.isLoading || state.loadingMore || pagina == null ||
            !pagina.hasNext)
            return

        cargarPagina(numero = pagina.number + 1, reemplazar = false)
    }

    private fun cargarPagina(numero: Int, reemplazar: Boolean)
    {
        _uiState.update {
            if (reemplazar)
                it.copy(
                    items = emptyList(),
                    page = null,
                    isLoading = true,
                    error = null,
                    loadingMore = false,
                    loadMoreError = null)
            else
                it.copy(
                    loadingMore = true,
                    loadMoreError = null)
        }

        viewModelScope.launch {
            try
            {
                val page = verGastos(
                    number = numero,
                    size = pageSize)

                _uiState.update {
                    it.copy(
                        items = if (reemplazar) page.content
                                else it.items + page.content,
                        page = page,
                        isLoading = false,
                        loadingMore = false,
                        error = null,
                        loadMoreError = null)
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadingMore = false)
                }
                throw e
            }
            catch (e: Exception)
            {
                val mensaje = e.message ?: if (reemplazar)
                    "Error al cargar"
                else
                    "Error al cargar más"

                _uiState.update {
                    if (reemplazar)
                        it.copy(
                            isLoading = false,
                            error = mensaje)
                    else
                        it.copy(
                            loadingMore = false,
                            loadMoreError = mensaje)
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
