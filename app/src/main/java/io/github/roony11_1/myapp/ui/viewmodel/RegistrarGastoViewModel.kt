package io.github.roony11_1.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.core.services.IClock
import io.github.roony11_1.myapp.domain.model.Categoria
import io.github.roony11_1.myapp.domain.model.Comercio
import io.github.roony11_1.myapp.domain.model.Cuenta
import io.github.roony11_1.myapp.domain.model.Etiqueta
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.ICategoriaRepository
import io.github.roony11_1.myapp.domain.repository.IComercioRepository
import io.github.roony11_1.myapp.domain.repository.ICuentaRepository
import io.github.roony11_1.myapp.domain.repository.IEtiquetaRepository
import io.github.roony11_1.myapp.domain.usecase.RegistrarGasto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val gastoGuardado: Gasto? = null,
    val fechaHora: Long,
    val comercioQuery: String = "",
    val comerciosRecomendados: List<Comercio> = emptyList(),
    val comercioSeleccionado: Comercio? = null,
    val etiquetaQuery: String = "",
    val etiquetasRecomendadas: List<Etiqueta> = emptyList(),
    val etiquetasSeleccionadas: List<Etiqueta> = emptyList(),
    val notas: String = "",
    val notasExpandido: Boolean = false,
    val fechaExpandido: Boolean = false,
    val creandoComercio: Boolean = false,
    val creandoEtiqueta: Boolean = false,
    val creandoCategoria: Boolean = false,
    val creandoCuenta: Boolean = false,
    val categoriaCreada: Categoria? = null,
    val cuentaCreada: Cuenta? = null)
{
    val ocupado: Boolean
        get() = cargandoOpciones ||
                guardando ||
                creandoComercio ||
                creandoEtiqueta ||
                creandoCategoria ||
                creandoCuenta
}

class RegistrarGastoViewModel(
    private val registrarGasto: RegistrarGasto,
    private val categoriaRepository: ICategoriaRepository,
    private val cuentaRepository: ICuentaRepository,
    private val comercioRepository: IComercioRepository,
    private val etiquetaRepository: IEtiquetaRepository,
    clock: IClock) : ViewModel()
{
    private val _uiState = MutableStateFlow(
        RegistrarGastoUiState(fechaHora = clock.now()))
    val uiState: StateFlow<RegistrarGastoUiState> = _uiState.asStateFlow()

    private var comercioJob: Job? = null
    private var etiquetaJob: Job? = null

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

    fun onFechaHoraChange(fechaHora: Long)
    {
        _uiState.update {
            it.copy(fechaHora = fechaHora)
        }
    }

    fun onComercioQueryChange(query: String)
    {
        comercioJob?.cancel()

        _uiState.update {
            it.copy(
                comercioQuery = query,
                comerciosRecomendados = emptyList())
        }

        if (query.isBlank())
            return

        comercioJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)

            try
            {
                val recomendados = comercioRepository.recomendar(query)

                if (_uiState.value.comercioQuery == query)
                {
                    _uiState.update {
                        it.copy(comerciosRecomendados = recomendados)
                    }
                }
            }
            catch (e: CancellationException)
            {
                throw e
            }
            catch (e: Exception)
            {
                if (_uiState.value.comercioQuery == query)
                {
                    _uiState.update {
                        it.copy(comerciosRecomendados = emptyList())
                    }
                }
            }
        }
    }

    fun onComercioSeleccionado(comercio: Comercio)
    {
        comercioJob?.cancel()

        _uiState.update {
            it.copy(
                comercioSeleccionado = comercio,
                comercioQuery = "",
                comerciosRecomendados = emptyList())
        }
    }

    fun onComercioLimpiar()
    {
        comercioJob?.cancel()

        _uiState.update {
            it.copy(
                comercioSeleccionado = null,
                comercioQuery = "",
                comerciosRecomendados = emptyList())
        }
    }

    fun onComercioCrear(nombre: String)
    {
        if (nombre.isBlank() || _uiState.value.creandoComercio)
            return

        comercioJob?.cancel()

        _uiState.update {
            it.copy(creandoComercio = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val comercio = comercioRepository.crear(nombre)

                _uiState.update {
                    it.copy(
                        creandoComercio = false,
                        comercioSeleccionado = comercio,
                        comercioQuery = "",
                        comerciosRecomendados = emptyList())
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(creandoComercio = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        creandoComercio = false,
                        error = e.message ?: "Error al crear comercio")
                }
            }
        }
    }

    fun onEtiquetaQueryChange(query: String)
    {
        etiquetaJob?.cancel()

        _uiState.update {
            it.copy(
                etiquetaQuery = query,
                etiquetasRecomendadas = emptyList())
        }

        if (query.isBlank())
            return

        etiquetaJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)

            try
            {
                val recomendadas = etiquetaRepository.recomendar(query)

                if (_uiState.value.etiquetaQuery == query)
                {
                    _uiState.update {
                        it.copy(etiquetasRecomendadas = recomendadas)
                    }
                }
            }
            catch (e: CancellationException)
            {
                throw e
            }
            catch (e: Exception)
            {
                if (_uiState.value.etiquetaQuery == query)
                {
                    _uiState.update {
                        it.copy(etiquetasRecomendadas = emptyList())
                    }
                }
            }
        }
    }

    fun onEtiquetaToggle(etiqueta: Etiqueta)
    {
        _uiState.update { state ->
            val seleccionadas = state.etiquetasSeleccionadas
            val existe = seleccionadas.any { it.nombre == etiqueta.nombre }

            state.copy(
                etiquetasSeleccionadas = if (existe)
                    seleccionadas.filterNot { it.nombre == etiqueta.nombre }
                else
                    seleccionadas + etiqueta)
        }
    }

    fun onEtiquetaCrear(nombre: String)
    {
        if (nombre.isBlank() || _uiState.value.creandoEtiqueta)
            return

        etiquetaJob?.cancel()

        _uiState.update {
            it.copy(creandoEtiqueta = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val etiqueta = etiquetaRepository.crear(nombre)

                _uiState.update { state ->
                    val yaSeleccionada = state.etiquetasSeleccionadas
                        .any { it.nombre == etiqueta.nombre }

                    state.copy(
                        creandoEtiqueta = false,
                        etiquetasSeleccionadas = if (yaSeleccionada)
                            state.etiquetasSeleccionadas
                        else
                            state.etiquetasSeleccionadas + etiqueta,
                        etiquetaQuery = "",
                        etiquetasRecomendadas = emptyList())
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(creandoEtiqueta = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        creandoEtiqueta = false,
                        error = e.message ?: "Error al crear etiqueta")
                }
            }
        }
    }

    fun onNotasChange(texto: String)
    {
        _uiState.update {
            it.copy(notas = texto)
        }
    }

    fun onNotasExpandirToggle()
    {
        _uiState.update {
            it.copy(notasExpandido = !it.notasExpandido)
        }
    }

    fun onFechaExpandirToggle()
    {
        _uiState.update {
            it.copy(fechaExpandido = !it.fechaExpandido)
        }
    }

    fun onCategoriaCrear(nombre: String)
    {
        if (nombre.isBlank() || _uiState.value.creandoCategoria)
            return

        _uiState.update {
            it.copy(creandoCategoria = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val categoria = categoriaRepository.crear(nombre)

                _uiState.update { state ->
                    state.copy(
                        creandoCategoria = false,
                        categorias = if (state.categorias.any {
                                it.id == categoria.id
                            })
                            state.categorias
                        else
                            state.categorias + categoria,
                        categoriaCreada = categoria)
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(creandoCategoria = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        creandoCategoria = false,
                        error = e.message ?: "Error al crear categoría")
                }
            }
        }
    }

    fun onCategoriaCreadaConsumida()
    {
        _uiState.update { it.copy(categoriaCreada = null) }
    }

    fun onCuentaCrear(nombre: String)
    {
        if (nombre.isBlank() || _uiState.value.creandoCuenta)
            return

        _uiState.update {
            it.copy(creandoCuenta = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val cuenta = cuentaRepository.crear(nombre)

                _uiState.update { state ->
                    state.copy(
                        creandoCuenta = false,
                        cuentas = if (state.cuentas.any { it.id == cuenta.id })
                            state.cuentas
                        else
                            state.cuentas + cuenta,
                        cuentaCreada = cuenta)
                }
            }
            catch (e: CancellationException)
            {
                _uiState.update { it.copy(creandoCuenta = false) }
                throw e
            }
            catch (e: Exception)
            {
                _uiState.update {
                    it.copy(
                        creandoCuenta = false,
                        error = e.message ?: "Error al crear cuenta")
                }
            }
        }
    }

    fun onCuentaCreadaConsumida()
    {
        _uiState.update { it.copy(cuentaCreada = null) }
    }

    fun onGuardar(
        monto: Double,
        descripcion: String,
        cuenta: Cuenta,
        categorias: List<Categoria>,
        fechaHora: Long,
        comercio: Comercio?,
        etiquetas: List<Etiqueta>,
        notas: String?)
    {
        if (_uiState.value.guardando)
            return

        comercioJob?.cancel()
        etiquetaJob?.cancel()

        _uiState.update {
            it.copy(guardando = true, error = null)
        }

        viewModelScope.launch {
            try
            {
                val gasto = registrarGasto(
                    monto = monto,
                    descripcion = descripcion,
                    cuenta = cuenta,
                    categorias = categorias,
                    fechaHora = fechaHora,
                    comercio = comercio,
                    etiquetas = etiquetas,
                    notas = notas)

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

    override fun onCleared()
    {
        comercioJob?.cancel()
        etiquetaJob?.cancel()
        super.onCleared()
    }

    companion object
    {
        private const val DEBOUNCE_MS = 250L

        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegistrarGastoViewModel(
                    registrarGasto = RegistrarGasto(
                        gastoRepository = container.gastoRepository),
                    categoriaRepository = container.categoriaRepository,
                    cuentaRepository = container.cuentaRepository,
                    comercioRepository = container.comercioRepository,
                    etiquetaRepository = container.etiquetaRepository,
                    clock = container.clock)
            }
        }
    }
}
