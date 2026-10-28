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
import io.github.roony11_1.myapp.domain.model.Categoria
import io.github.roony11_1.myapp.domain.model.Comercio
import io.github.roony11_1.myapp.domain.model.Cuenta
import io.github.roony11_1.myapp.domain.model.EstadoGasto
import io.github.roony11_1.myapp.domain.model.Etiqueta
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.model.GastosFiltros
import io.github.roony11_1.myapp.domain.model.ResumenMes
import io.github.roony11_1.myapp.domain.repository.ICategoriaRepository
import io.github.roony11_1.myapp.domain.repository.IComercioRepository
import io.github.roony11_1.myapp.domain.repository.ICuentaRepository
import io.github.roony11_1.myapp.domain.repository.IEtiquetaRepository
import io.github.roony11_1.myapp.domain.usecase.AnularGasto
import io.github.roony11_1.myapp.domain.usecase.ObtenerResumenMes
import io.github.roony11_1.myapp.domain.usecase.VerGastos
import java.time.YearMonth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialFiltrosUi(
    val anio: Int,
    val mes: Int,
    val cuentaId: Long? = null,
    val categoriaId: Long? = null,
    val etiquetaId: Long? = null,
    val comercioId: Long? = null,
    val descripcion: String = "")

data class OpcionesHistorialUi(
    val cuentas: List<Cuenta> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val etiquetas: List<Etiqueta> = emptyList(),
    val comercios: List<Comercio> = emptyList())

class VerGastosViewModel(
    private val verGastos: VerGastos,
    private val obtenerResumenMes: ObtenerResumenMes,
    private val anularGasto: AnularGasto,
    private val cuentaRepository: ICuentaRepository,
    private val categoriaRepository: ICategoriaRepository,
    private val etiquetaRepository: IEtiquetaRepository,
    private val comercioRepository: IComercioRepository,
    anioInicial: Int?,
    mesInicial: Int?,
    clock: IClock) : ViewModel()
{
    private val mesBase = if (anioInicial != null && mesInicial != null)
        YearMonth.of(anioInicial, mesInicial)
    else
        anioMesLocal(clock.now())

    private val _uiState = MutableStateFlow(PagedUiState<Gasto>())
    val uiState: StateFlow<PagedUiState<Gasto>> = _uiState.asStateFlow()

    private val _filtros = MutableStateFlow(
        HistorialFiltrosUi(
            anio = mesBase.year,
            mes = mesBase.monthValue))
    val filtros: StateFlow<HistorialFiltrosUi> = _filtros.asStateFlow()

    private val _opciones = MutableStateFlow(OpcionesHistorialUi())
    val opciones: StateFlow<OpcionesHistorialUi> = _opciones.asStateFlow()

    private val _resumen = MutableStateFlow<ResumenMes?>(null)
    val resumen: StateFlow<ResumenMes?> = _resumen.asStateFlow()

    private val _anulandoId = MutableStateFlow<Long?>(null)
    val anulandoId: StateFlow<Long?> = _anulandoId.asStateFlow()

    private val _errorAnulacion = MutableStateFlow<String?>(null)
    val errorAnulacion: StateFlow<String?> = _errorAnulacion.asStateFlow()

    private var cargaJob: Job? = null
    private var descripcionJob: Job? = null

    init
    {
        cargarOpciones()
    }

    fun cargar()
    {
        val state = _uiState.value
        if (state.isLoading || state.loadingMore || state.page != null)
            return

        cargarPagina(numero = 0, reemplazar = true)
    }

    fun recargar()
    {
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

    fun onDescripcionChange(texto: String)
    {
        _filtros.update { it.copy(descripcion = texto) }
        descripcionJob?.cancel()

        descripcionJob = viewModelScope.launch {
            delay(300)
            cargarPagina(numero = 0, reemplazar = true)
        }
    }

    fun onCuentaChange(id: Long?)
    {
        descripcionJob?.cancel()
        _filtros.update { it.copy(cuentaId = id) }
        recargar()
    }

    fun onCategoriaChange(id: Long?)
    {
        descripcionJob?.cancel()
        _filtros.update { it.copy(categoriaId = id) }
        recargar()
    }

    fun onEtiquetaChange(id: Long?)
    {
        descripcionJob?.cancel()
        _filtros.update { it.copy(etiquetaId = id) }
        recargar()
    }

    fun onComercioChange(id: Long?)
    {
        descripcionJob?.cancel()
        _filtros.update { it.copy(comercioId = id) }
        recargar()
    }

    fun limpiarFiltros()
    {
        descripcionJob?.cancel()
        _filtros.update {
            it.copy(
                cuentaId = null,
                categoriaId = null,
                etiquetaId = null,
                comercioId = null,
                descripcion = "")
        }
        recargar()
    }

    fun mesAnterior()
    {
        cambiarMes(-1)
    }

    fun mesSiguiente()
    {
        cambiarMes(1)
    }

    private fun cambiarMes(delta: Long)
    {
        descripcionJob?.cancel()
        val filtros = _filtros.value
        val destino = YearMonth.of(filtros.anio, filtros.mes).plusMonths(delta)

        _filtros.update {
            it.copy(
                anio = destino.year,
                mes = destino.monthValue)
        }

        _resumen.value = null
        recargar()
    }

    fun anular(gasto: Gasto)
    {
        if (gasto.estado == EstadoGasto.ANULADO || _anulandoId.value != null)
            return

        _anulandoId.value = gasto.id
        _errorAnulacion.value = null

        viewModelScope.launch {
            try
            {
                val actualizado = anularGasto(gasto.id)
                val filtros = _filtros.value
                val desde = inicioMes(filtros.anio, filtros.mes)
                val hasta = inicioMesSiguiente(filtros.anio, filtros.mes)

                _uiState.update {
                    it.copy(
                        items = it.items.map { item ->
                            if (item.id == actualizado.id) actualizado else item
                        })
                }

                _resumen.value = obtenerResumenMes(desde, hasta)
            }
            catch (e: CancellationException)
            {
                throw e
            }
            catch (e: Exception)
            {
                _errorAnulacion.value = e.message ?: "No se pudo anular el gasto"
            }
            finally
            {
                _anulandoId.value = null
            }
        }
    }

    private fun cargarOpciones()
    {
        viewModelScope.launch {
            try
            {
                val cuentas = cuentaRepository.findAll()
                val categorias = categoriaRepository.findAll()
                val etiquetas = etiquetaRepository.findAll()
                val comercios = comercioRepository.findAll()

                _opciones.value = OpcionesHistorialUi(
                    cuentas = cuentas,
                    categorias = categorias,
                    etiquetas = etiquetas,
                    comercios = comercios)
            }
            catch (e: CancellationException)
            {
                throw e
            }
            catch (_: Exception)
            {
            }
        }
    }

    private fun cargarPagina(numero: Int, reemplazar: Boolean)
    {
        cargaJob?.cancel()

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

        cargaJob = viewModelScope.launch {
            try
            {
                val filtrosUi = _filtros.value
                val desde = inicioMes(filtrosUi.anio, filtrosUi.mes)
                val hasta = inicioMesSiguiente(filtrosUi.anio, filtrosUi.mes)
                val filtros = GastosFiltros(
                    desde = desde,
                    hastaExclusivo = hasta,
                    cuentaId = filtrosUi.cuentaId,
                    categoriaId = filtrosUi.categoriaId,
                    etiquetaId = filtrosUi.etiquetaId,
                    comercioId = filtrosUi.comercioId,
                    descripcion = filtrosUi.descripcion)

                val page = verGastos(
                    filtros = filtros,
                    number = numero,
                    size = PAGE_SIZE)
                val resumen = obtenerResumenMes(desde, hasta)

                _resumen.value = resumen
                _uiState.update {
                    it.copy(
                        items = if (reemplazar) page.content
                                else (it.items + page.content)
                                    .distinctBy { gasto -> gasto.id },
                        page = page,
                        isLoading = false,
                        loadingMore = false,
                        error = null,
                        loadMoreError = null)
                }
            }
            catch (e: CancellationException)
            {
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

    override fun onCleared()
    {
        descripcionJob?.cancel()
        cargaJob?.cancel()
        super.onCleared()
    }

    companion object
    {
        private const val PAGE_SIZE = 20

        fun factory(
            container: AppContainer,
            anioInicial: Int?,
            mesInicial: Int?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                VerGastosViewModel(
                    verGastos = VerGastos(container.gastoRepository),
                    obtenerResumenMes = ObtenerResumenMes(container.gastoRepository),
                    anularGasto = AnularGasto(container.gastoRepository),
                    cuentaRepository = container.cuentaRepository,
                    categoriaRepository = container.categoriaRepository,
                    etiquetaRepository = container.etiquetaRepository,
                    comercioRepository = container.comercioRepository,
                    anioInicial = anioInicial,
                    mesInicial = mesInicial,
                    clock = container.clock)
            }
        }
    }
}
