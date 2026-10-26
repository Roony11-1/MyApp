package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import io.github.roony11_1.myapp.ui.components.BotonAgregar
import io.github.roony11_1.myapp.ui.components.BotonPrincipal
import io.github.roony11_1.myapp.ui.components.CampoTextoPremium
import io.github.roony11_1.myapp.ui.components.ChipPremium
import io.github.roony11_1.myapp.ui.components.EncabezadoPantalla
import io.github.roony11_1.myapp.ui.components.SelectorFechaHora
import io.github.roony11_1.myapp.ui.components.TarjetaNotas
import io.github.roony11_1.myapp.ui.components.TarjetaSeccion
import io.github.roony11_1.myapp.ui.viewmodel.RegistrarGastoViewModel

private enum class CampoCreacion
{
    COMERCIO,
    CATEGORIA,
    CUENTA
}

@Composable
fun RegistrarGastoScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrarGastoViewModel,
    onGuardadoExitoso: () -> Unit,
    onBack: () -> Unit)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val error = state.error

    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var categoriasSeleccionadas by remember {
        mutableStateOf(setOf<Long>(3L))
    }

    var cuentaSeleccionadaId by remember {
        mutableStateOf<Long>(1L)
    }

    var campoActivo by remember { mutableStateOf<CampoCreacion?>(null) }
    var nuevaCategoria by remember { mutableStateOf("") }
    var nuevaCuenta by remember { mutableStateOf("") }

    val comercioFocusRequester = remember { FocusRequester() }
    val categoriaFocusRequester = remember { FocusRequester() }
    val cuentaFocusRequester = remember { FocusRequester() }

    LaunchedEffect(campoActivo) {
        when (campoActivo)
        {
            CampoCreacion.COMERCIO ->
            {
                withFrameNanos { }
                comercioFocusRequester.requestFocus()
            }

            CampoCreacion.CATEGORIA ->
            {
                withFrameNanos { }
                categoriaFocusRequester.requestFocus()
            }

            CampoCreacion.CUENTA ->
            {
                withFrameNanos { }
                cuentaFocusRequester.requestFocus()
            }

            null -> Unit
        }
    }

    LaunchedEffect(state.gastoGuardado) {
        if (state.gastoGuardado != null)
        {
            viewModel.onExitoConsumido()
            onGuardadoExitoso()
        }
    }

    LaunchedEffect(state.categoriaCreada) {
        state.categoriaCreada?.let { categoria ->
            categoriasSeleccionadas = categoriasSeleccionadas + categoria.id
            if (campoActivo == CampoCreacion.CATEGORIA)
                campoActivo = null
            nuevaCategoria = ""
            viewModel.onCategoriaCreadaConsumida()
        }
    }

    LaunchedEffect(state.cuentaCreada) {
        state.cuentaCreada?.let { cuenta ->
            cuentaSeleccionadaId = cuenta.id
            if (campoActivo == CampoCreacion.CUENTA)
                campoActivo = null
            nuevaCuenta = ""
            viewModel.onCuentaCreadaConsumida()
        }
    }

    LaunchedEffect(state.comercioSeleccionado) {
        if (state.comercioSeleccionado != null &&
            campoActivo == CampoCreacion.COMERCIO)
            campoActivo = null
    }

    val comercioQueryNormalizada = normalizarNombre(state.comercioQuery)
    val mostrarCrearComercio = campoActivo == CampoCreacion.COMERCIO &&
            comercioQueryNormalizada.isNotEmpty() &&
            state.comercioSeleccionado == null &&
            state.comerciosRecomendados.none {
                it.nombre == comercioQueryNormalizada
            }

    val categoriaQueryNormalizada = normalizarNombre(nuevaCategoria)
    val mostrarCrearCategoria = campoActivo == CampoCreacion.CATEGORIA &&
            categoriaQueryNormalizada.isNotEmpty() &&
            state.categorias.none {
                normalizarNombre(it.nombre) == categoriaQueryNormalizada
            }

    val cuentaQueryNormalizada = normalizarNombre(nuevaCuenta)
    val mostrarCrearCuenta = campoActivo == CampoCreacion.CUENTA &&
            cuentaQueryNormalizada.isNotEmpty() &&
            state.cuentas.none {
                normalizarNombre(it.nombre) == cuentaQueryNormalizada
            }

    val etiquetaQueryNormalizada = normalizarNombre(state.etiquetaQuery)
    val mostrarCrearEtiqueta = etiquetaQueryNormalizada.isNotEmpty() &&
            state.etiquetasRecomendadas.none {
                it.nombre == etiquetaQueryNormalizada
            } &&
            state.etiquetasSeleccionadas.none {
                it.nombre == etiquetaQueryNormalizada
            }

    val etiquetasRecomendadas = state.etiquetasRecomendadas.filterNot {
        recomendada -> state.etiquetasSeleccionadas.any {
            it.nombre == recomendada.nombre
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp))
    {
        EncabezadoPantalla(
            titulo = "Registrar gasto",
            eyebrow = "Nuevo movimiento",
            subtitulo = "Completa los datos y guarda en segundos.",
            onBack = onBack)

        TarjetaSeccion(
            titulo = "Detalle",
            icono = Icons.Rounded.Notes,
            descripcion = "Lo esencial del movimiento")
        {
            CampoTextoPremium(
                value = monto,
                onValueChange = { monto = it },
                label = "Monto",
                leadingIcon = Icons.Rounded.Payments,
                placeholder = "0,00",
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal),
                isError = error != null)

            CampoTextoPremium(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = "Descripción",
                leadingIcon = Icons.Rounded.Notes,
                placeholder = "¿En qué se fue?",
                minLines = 3,
                isError = error != null)
        }

        TarjetaSeccion(
            titulo = "Categorías",
            icono = Icons.Rounded.Category,
            descripcion = "Puedes elegir más de una",
            accion = {
                BotonAgregar(
                    onClick = {
                        if (campoActivo == CampoCreacion.CATEGORIA)
                        {
                            campoActivo = null
                            nuevaCategoria = ""
                        }
                        else
                        {
                            campoActivo = CampoCreacion.CATEGORIA
                        }
                    },
                    enabled = !state.ocupado,
                    contentDescription = "Agregar categoría")
            })
        {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                state.categorias.forEach { categoria ->
                    ChipPremium(
                        texto = categoria.nombre,
                        seleccionado = categoria.id in categoriasSeleccionadas,
                        onClick = {
                            categoriasSeleccionadas =
                                if (categoria.id in categoriasSeleccionadas)
                                    categoriasSeleccionadas - categoria.id
                                else
                                    categoriasSeleccionadas + categoria.id
                        },
                        enabled = !state.ocupado)
                }
            }

            if (campoActivo == CampoCreacion.CATEGORIA)
            {
                CampoTextoPremium(
                    value = nuevaCategoria,
                    onValueChange = { nuevaCategoria = it },
                    label = "Nueva categoría",
                    leadingIcon = Icons.Rounded.Category,
                    placeholder = "Ej. Comida rápida",
                    singleLine = true,
                    modifier = Modifier.focusRequester(categoriaFocusRequester))

                if (mostrarCrearCategoria)
                {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        ChipPremium(
                            texto = "Crear \"${nuevaCategoria.trim()}\"",
                            seleccionado = false,
                            onClick = {
                                viewModel.onCategoriaCrear(nuevaCategoria)
                            },
                            enabled = !state.ocupado,
                            icono = Icons.Rounded.Add)
                    }
                }
            }
        }

        TarjetaSeccion(
            titulo = "Cuenta",
            icono = Icons.Rounded.AccountBalanceWallet,
            descripcion = "¿De dónde salió el dinero?",
            accion = {
                BotonAgregar(
                    onClick = {
                        if (campoActivo == CampoCreacion.CUENTA)
                        {
                            campoActivo = null
                            nuevaCuenta = ""
                        }
                        else
                        {
                            campoActivo = CampoCreacion.CUENTA
                        }
                    },
                    enabled = !state.ocupado,
                    contentDescription = "Agregar cuenta")
            })
        {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                state.cuentas.forEach { cuenta ->
                    ChipPremium(
                        texto = cuenta.nombre,
                        seleccionado = cuenta.id == cuentaSeleccionadaId,
                        onClick = { cuentaSeleccionadaId = cuenta.id },
                        enabled = !state.ocupado)
                }
            }

            if (campoActivo == CampoCreacion.CUENTA)
            {
                CampoTextoPremium(
                    value = nuevaCuenta,
                    onValueChange = { nuevaCuenta = it },
                    label = "Nueva cuenta",
                    leadingIcon = Icons.Rounded.AccountBalanceWallet,
                    placeholder = "Ej. Efectivo",
                    singleLine = true,
                    modifier = Modifier.focusRequester(cuentaFocusRequester))

                if (mostrarCrearCuenta)
                {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        ChipPremium(
                            texto = "Crear \"${nuevaCuenta.trim()}\"",
                            seleccionado = false,
                            onClick = {
                                viewModel.onCuentaCrear(nuevaCuenta)
                            },
                            enabled = !state.ocupado,
                            icono = Icons.Rounded.Add)
                    }
                }
            }
        }

        TarjetaSeccion(
            titulo = "Comercio",
            icono = Icons.Rounded.Store,
            descripcion = "Opcional",
            accion = {
                BotonAgregar(
                    onClick = {
                        if (campoActivo == CampoCreacion.COMERCIO)
                        {
                            campoActivo = null
                            viewModel.onComercioQueryChange("")
                        }
                        else
                        {
                            campoActivo = CampoCreacion.COMERCIO
                        }
                    },
                    enabled = !state.ocupado,
                    contentDescription = "Agregar comercio")
            })
        {
            state.comercioSeleccionado?.let { comercio ->
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    ChipPremium(
                        texto = comercio.nombre,
                        seleccionado = true,
                        onClick = viewModel::onComercioLimpiar,
                        enabled = !state.ocupado,
                        iconoFinal = Icons.Rounded.Close)
                }
            }

            if (campoActivo == CampoCreacion.COMERCIO)
            {
                CampoTextoPremium(
                    value = state.comercioQuery,
                    onValueChange = viewModel::onComercioQueryChange,
                    label = "Comercio",
                    leadingIcon = Icons.Rounded.Store,
                    placeholder = "Ej. Supermercado",
                    singleLine = true,
                    modifier = Modifier.focusRequester(comercioFocusRequester))

                if (state.comerciosRecomendados.isNotEmpty())
                {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        state.comerciosRecomendados.forEach { comercio ->
                            ChipPremium(
                                texto = comercio.nombre,
                                seleccionado = false,
                                onClick = {
                                    viewModel.onComercioSeleccionado(comercio)
                                },
                                enabled = !state.ocupado)
                        }
                    }
                }

                if (mostrarCrearComercio)
                {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        ChipPremium(
                            texto = "Crear \"${state.comercioQuery.trim()}\"",
                            seleccionado = false,
                            onClick = {
                                viewModel.onComercioCrear(state.comercioQuery)
                            },
                            enabled = !state.ocupado,
                            icono = Icons.Rounded.Add)
                    }
                }
            }
        }

        TarjetaSeccion(
            titulo = "Etiquetas",
            icono = Icons.Rounded.Label,
            descripcion = "Opcional, puedes agregar varias")
        {
            CampoTextoPremium(
                value = state.etiquetaQuery,
                onValueChange = viewModel::onEtiquetaQueryChange,
                label = "Etiqueta",
                leadingIcon = Icons.Rounded.Label,
                placeholder = "Ej. Trabajo",
                singleLine = true)

            if (etiquetasRecomendadas.isNotEmpty())
            {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    etiquetasRecomendadas.forEach { etiqueta ->
                        ChipPremium(
                            texto = etiqueta.nombre,
                            seleccionado = false,
                            onClick = {
                                viewModel.onEtiquetaToggle(etiqueta)
                            },
                            enabled = !state.ocupado)
                    }
                }
            }

            if (mostrarCrearEtiqueta)
            {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    ChipPremium(
                        texto = "Crear \"${state.etiquetaQuery.trim()}\"",
                        seleccionado = false,
                        onClick = {
                            viewModel.onEtiquetaCrear(state.etiquetaQuery)
                        },
                        enabled = !state.ocupado,
                        icono = Icons.Rounded.Add)
                }
            }

            if (state.etiquetasSeleccionadas.isNotEmpty())
            {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    state.etiquetasSeleccionadas.forEach { etiqueta ->
                        ChipPremium(
                            texto = etiqueta.nombre,
                            seleccionado = true,
                            onClick = {
                                viewModel.onEtiquetaToggle(etiqueta)
                            },
                            enabled = !state.ocupado,
                            iconoFinal = Icons.Rounded.Close)
                    }
                }
            }
        }

        TarjetaNotas(
            notas = state.notas,
            expandido = state.notasExpandido,
            onExpandirToggle = viewModel::onNotasExpandirToggle,
            onNotasChange = viewModel::onNotasChange,
            enabled = !state.ocupado)

        SelectorFechaHora(
            fechaHora = state.fechaHora,
            onFechaHoraChange = viewModel::onFechaHoraChange,
            enabled = !state.ocupado,
            expandido = state.fechaExpandido,
            onExpandirToggle = viewModel::onFechaExpandirToggle)

        if (error != null)
        {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.errorContainer,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.25f)))
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(18.dp))

                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f))
                }
            }
        }

        BotonPrincipal(
            texto = "Guardar gasto",
            icono = Icons.Rounded.Check,
            cargando = state.guardando,
            enabled = !state.ocupado,
            onClick = {
                val montoDouble = monto.toDoubleOrNull()
                val cuentaGuardar = state.cuentas.find {
                    it.id == cuentaSeleccionadaId
                }

                if (montoDouble != null && montoDouble > 0 && cuentaGuardar != null)
                {
                    val categoriasGuardar = state.categorias.filter {
                        it.id in categoriasSeleccionadas
                    }

                    viewModel.onGuardar(
                        monto = montoDouble,
                        descripcion = descripcion,
                        cuenta = cuentaGuardar,
                        categorias = categoriasGuardar,
                        fechaHora = state.fechaHora,
                        comercio = state.comercioSeleccionado,
                        etiquetas = state.etiquetasSeleccionadas,
                        notas = state.notas)
                }
            })
    }
}
