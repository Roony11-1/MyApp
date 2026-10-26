package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.domain.model.EstadoGasto
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.usecase.AnularGasto
import io.github.roony11_1.myapp.domain.usecase.VerGasto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerGastoViewModel(
    private val verGasto: VerGasto,
    private val anularGasto: AnularGasto,
    private val gastoId: Long) : ViewModel()
{
    private val _uiState = MutableStateFlow<UiState<Gasto?>>(UiState.Idle)
    val uiState: StateFlow<UiState<Gasto?>> = _uiState.asStateFlow()

    private val _anulando = MutableStateFlow(false)
    val anulando: StateFlow<Boolean> = _anulando.asStateFlow()

    private val _errorAnulacion = MutableStateFlow<String?>(null)
    val errorAnulacion: StateFlow<String?> = _errorAnulacion.asStateFlow()

    fun cargar()
    {
        val state = _uiState.value
        if (state is UiState.Cargando || state is UiState.Exito)
            return

        _uiState.value = UiState.Cargando

        viewModelScope.launch {
            try
            {
                _uiState.value = UiState.Exito(verGasto(gastoId))
            }
            catch (e: CancellationException)
            {
                _uiState.value = UiState.Idle
                throw e
            }
            catch (e: Exception)
            {
                _uiState.value = UiState.Error(
                    e.message ?: "Error al cargar")
            }
        }
    }

    fun anular()
    {
        val state = _uiState.value
        val gasto = (state as? UiState.Exito)?.data ?: return

        if (gasto.estado == EstadoGasto.ANULADO || _anulando.value)
            return

        _anulando.value = true
        _errorAnulacion.value = null

        viewModelScope.launch {
            try
            {
                _uiState.value = UiState.Exito(anularGasto(gastoId))
            }
            catch (e: CancellationException)
            {
                throw e
            }
            catch (e: Exception)
            {
                _errorAnulacion.value = e.message ?: "No se pudo anular el gasto"

                try
                {
                    val actual = verGasto(gastoId)

                    if (actual?.estado == EstadoGasto.ANULADO)
                    {
                        _uiState.value = UiState.Exito(actual)
                        _errorAnulacion.value = null
                    }
                }
                catch (reload: CancellationException)
                {
                    throw reload
                }
                catch (_: Exception)
                {
                }
            }
            finally
            {
                _anulando.value = false
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
                    verGasto = VerGasto(container.gastoRepository),
                    anularGasto = AnularGasto(container.gastoRepository),
                    gastoId = gastoId)
            }
        }
    }
}
