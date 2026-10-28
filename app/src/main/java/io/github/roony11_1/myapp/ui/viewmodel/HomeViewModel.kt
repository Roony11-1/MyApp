package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.core.services.IClock
import io.github.roony11_1.myapp.core.util.anioMesLocal
import io.github.roony11_1.myapp.core.util.inicioMes
import io.github.roony11_1.myapp.core.util.inicioMesSiguiente
import io.github.roony11_1.myapp.domain.model.MesHistorial
import io.github.roony11_1.myapp.domain.model.ResumenMes
import io.github.roony11_1.myapp.domain.repository.IPeriodoHistorialRepository
import io.github.roony11_1.myapp.domain.usecase.ObtenerHistorialMensual
import io.github.roony11_1.myapp.domain.usecase.ObtenerResumenMes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val anioActual: Int = 0,
    val mesActual: Int = 0,
    val resumenActual: ResumenMes? = null,
    val meses: List<MesHistorial> = emptyList(),
    val periodoMeses: Int? = 6,
    val cargando: Boolean = false,
    val error: String? = null)

class HomeViewModel(
    private val obtenerResumenMes: ObtenerResumenMes,
    private val obtenerHistorialMensual: ObtenerHistorialMensual,
    private val periodoHistorialRepository: IPeriodoHistorialRepository,
    private val clock: IClock) : ViewModel()
{
    private val _uiState = MutableStateFlow(
        HomeUiState(periodoMeses = periodoHistorialRepository.obtenerMeses()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var cargaJob: Job? = null

    init
    {
        cargar()
    }

    fun cargar()
    {
        if (_uiState.value.cargando)
            return

        cargarDatos()
    }

    fun recargar()
    {
        if (_uiState.value.cargando)
            return

        cargarDatos()
    }

    fun cambiarPeriodo(meses: Int?)
    {
        periodoHistorialRepository.guardarMeses(meses)

        _uiState.update {
            it.copy(periodoMeses = periodoHistorialRepository.obtenerMeses())
        }

        cargarDatos()
    }

    private fun cargarDatos()
    {
        val periodo = periodoHistorialRepository.obtenerMeses()
        val mesActual = anioMesLocal(clock.now())
        val desdeMes = inicioMes(mesActual.year, mesActual.monthValue)
        val hastaMes = inicioMesSiguiente(mesActual.year, mesActual.monthValue)
        val desdeHistorial = periodo?.let {
            val inicio = mesActual.minusMonths((it - 1).coerceAtLeast(0).toLong())
            inicioMes(inicio.year, inicio.monthValue)
        }

        cargaJob?.cancel()

        _uiState.update {
            it.copy(
                anioActual = mesActual.year,
                mesActual = mesActual.monthValue,
                periodoMeses = periodo,
                cargando = true,
                error = null)
        }

        cargaJob = viewModelScope.launch {
            try
            {
                val resumen = obtenerResumenMes(desdeMes, hastaMes)
                val meses = obtenerHistorialMensual(desdeHistorial, hastaMes)

                _uiState.update {
                    it.copy(
                        resumenActual = resumen,
                        meses = meses,
                        cargando = false,
                        error = null)
                }
            }
            catch (e: CancellationException)
            {
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = e.message ?: "No se pudo cargar el inicio")
                }
            }
        }
    }

    override fun onCleared()
    {
        cargaJob?.cancel()
        super.onCleared()
    }

    companion object
    {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(
                    obtenerResumenMes = ObtenerResumenMes(container.gastoRepository),
                    obtenerHistorialMensual = ObtenerHistorialMensual(
                        container.gastoRepository),
                    periodoHistorialRepository = container.periodoHistorialRepository,
                    clock = container.clock)
            }
        }
    }
}
