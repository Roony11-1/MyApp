package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.core.util.claveDia
import io.github.roony11_1.myapp.core.util.formatearEncabezadoDia
import io.github.roony11_1.myapp.core.util.formatearFechaHora
import io.github.roony11_1.myapp.core.util.formatearMes
import io.github.roony11_1.myapp.core.util.formatearMonto
import io.github.roony11_1.myapp.domain.model.EstadoGasto
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.model.ResumenMes
import io.github.roony11_1.myapp.ui.components.CampoTextoPremium
import io.github.roony11_1.myapp.ui.components.ChipPremium
import io.github.roony11_1.myapp.ui.components.EncabezadoPantalla
import io.github.roony11_1.myapp.ui.components.EstadoGastoBadge
import io.github.roony11_1.myapp.ui.components.UiStateContent
import io.github.roony11_1.myapp.ui.viewmodel.HistorialFiltrosUi
import io.github.roony11_1.myapp.ui.viewmodel.OpcionesHistorialUi
import io.github.roony11_1.myapp.ui.viewmodel.PagedData
import io.github.roony11_1.myapp.ui.viewmodel.VerGastosViewModel
import kotlin.math.roundToInt

private enum class TipoFiltro
{
    CUENTA,
    CATEGORIA,
    ETIQUETA,
    COMERCIO
}

private data class OpcionFiltro(
    val id: Long?,
    val nombre: String,
    val activa: Boolean = true)

@Composable
fun VerGastosScreen(
    modifier: Modifier = Modifier,
    viewModel: VerGastosViewModel,
    onGastoClick: (Long) -> Unit,
    onBack: () -> Unit)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filtros by viewModel.filtros.collectAsStateWithLifecycle()
    val opciones by viewModel.opciones.collectAsStateWithLifecycle()
    val resumen by viewModel.resumen.collectAsStateWithLifecycle()
    val anulandoId by viewModel.anulandoId.collectAsStateWithLifecycle()
    val errorAnulacion by viewModel.errorAnulacion.collectAsStateWithLifecycle()

    var filtroAbierto by remember { mutableStateOf<TipoFiltro?>(null) }
    var gastoAAnular by remember { mutableStateOf<Gasto?>(null) }

    LifecycleResumeEffect(Unit) {
        viewModel.recargar()

        onPauseOrDispose { }
    }

    Column(modifier = modifier.fillMaxSize())
    {
        EncabezadoPantalla(
            titulo = "Historial",
            eyebrow = "Gastos del mes",
            subtitulo = "Filtra y revisa tus movimientos.",
            onBack = onBack,
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 12.dp))

        SelectorMes(
            filtros = filtros,
            onMesAnterior = viewModel::mesAnterior,
            onMesSiguiente = viewModel::mesSiguiente,
            modifier = Modifier.padding(horizontal = 20.dp))

        FiltrosHistorial(
            filtros = filtros,
            opciones = opciones,
            onDescripcionChange = viewModel::onDescripcionChange,
            onAbrirFiltro = { filtroAbierto = it },
            onLimpiar = viewModel::limpiarFiltros,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))

        ResumenGastos(
            resumen = resumen,
            pagedData = state.data,
            filtros = filtros,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))

        errorAnulacion?.let { mensaje ->
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
        }

        UiStateContent<PagedData<Gasto>>(
            state = state,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            errorContent = { mensaje ->
                ErrorCargaGastos(
                    mensaje = mensaje,
                    onReintentar = viewModel::recargar)
            })
        { pagedData ->
            ContenidoGastos(
                pagedData = pagedData,
                filtros = filtros,
                anulandoId = anulandoId,
                onGastoClick = onGastoClick,
                onAnularSolicitado = { gastoAAnular = it },
                onCargarMas = viewModel::cargarMas)
        }
    }

    filtroAbierto?.let { tipo ->
        SelectorFiltroDialog(
            tipo = tipo,
            opciones = opcionesPara(tipo, opciones),
            seleccionadoId = idSeleccionado(tipo, filtros),
            onSeleccionar = { id ->
                when (tipo)
                {
                    TipoFiltro.CUENTA -> viewModel.onCuentaChange(id)
                    TipoFiltro.CATEGORIA -> viewModel.onCategoriaChange(id)
                    TipoFiltro.ETIQUETA -> viewModel.onEtiquetaChange(id)
                    TipoFiltro.COMERCIO -> viewModel.onComercioChange(id)
                }

                filtroAbierto = null
            },
            onCerrar = { filtroAbierto = null })
    }

    gastoAAnular?.let { gasto ->
        AlertDialog(
            onDismissRequest = { gastoAAnular = null },
            title = {
                Text(text = "Anular gasto")
            },
            text = {
                Text(
                    text = "El gasto no se eliminará. Quedará en el " +
                            "historial marcado como anulado.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        gastoAAnular = null
                        viewModel.anular(gasto)
                    },
                    enabled = anulandoId == null,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error))
                {
                    Text(text = "Anular")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { gastoAAnular = null },
                    enabled = anulandoId == null)
                {
                    Text(text = "Cancelar")
                }
            })
    }
}

@Composable
private fun SelectorMes(
    filtros: HistorialFiltrosUi,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    modifier: Modifier = Modifier)
{
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
    {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            IconButton(onClick = onMesAnterior)
            {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = "Mes anterior",
                    tint = MaterialTheme.colorScheme.primary)
            }

            Text(
                text = formatearMes(filtros.anio, filtros.mes),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f))

            IconButton(onClick = onMesSiguiente)
            {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Mes siguiente",
                    tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun FiltrosHistorial(
    filtros: HistorialFiltrosUi,
    opciones: OpcionesHistorialUi,
    onDescripcionChange: (String) -> Unit,
    onAbrirFiltro: (TipoFiltro) -> Unit,
    onLimpiar: () -> Unit,
    modifier: Modifier = Modifier)
{
    val hayFiltros = filtros.cuentaId != null ||
            filtros.categoriaId != null ||
            filtros.etiquetaId != null ||
            filtros.comercioId != null ||
            filtros.descripcion.isNotBlank()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp))
    {
        CampoTextoPremium(
            value = filtros.descripcion,
            onValueChange = onDescripcionChange,
            label = "Buscar descripción",
            leadingIcon = Icons.Rounded.Search,
            placeholder = "Ej. supermercado",
            singleLine = true)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp))
        {
            ChipPremium(
                texto = opciones.cuentas
                    .firstOrNull { it.id == filtros.cuentaId }?.nombre
                    ?: "Cuenta",
                seleccionado = filtros.cuentaId != null,
                onClick = { onAbrirFiltro(TipoFiltro.CUENTA) })

            ChipPremium(
                texto = opciones.categorias
                    .firstOrNull { it.id == filtros.categoriaId }?.nombre
                    ?: "Categoría",
                seleccionado = filtros.categoriaId != null,
                onClick = { onAbrirFiltro(TipoFiltro.CATEGORIA) })

            ChipPremium(
                texto = opciones.etiquetas
                    .firstOrNull { it.id == filtros.etiquetaId }?.nombre
                    ?: "Etiqueta",
                seleccionado = filtros.etiquetaId != null,
                onClick = { onAbrirFiltro(TipoFiltro.ETIQUETA) })

            ChipPremium(
                texto = opciones.comercios
                    .firstOrNull { it.id == filtros.comercioId }?.nombre
                    ?: "Comercio",
                seleccionado = filtros.comercioId != null,
                onClick = { onAbrirFiltro(TipoFiltro.COMERCIO) })

            if (hayFiltros)
            {
                ChipPremium(
                    texto = "Limpiar",
                    seleccionado = false,
                    onClick = onLimpiar,
                    icono = Icons.Rounded.Close)
            }
        }
    }
}

@Composable
private fun SelectorFiltroDialog(
    tipo: TipoFiltro,
    opciones: List<OpcionFiltro>,
    seleccionadoId: Long?,
    onSeleccionar: (Long?) -> Unit,
    onCerrar: () -> Unit)
{
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(text = tituloFiltro(tipo))
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp))
            {
                item(key = "todas") {
                    FilaOpcionFiltro(
                        opcion = OpcionFiltro(
                            id = null,
                            nombre = textoTodos(tipo)),
                        seleccionado = seleccionadoId == null,
                        onClick = { onSeleccionar(null) })
                }

                items(
                    items = opciones,
                    key = { "opcion-${it.id}" }) { opcion ->
                    FilaOpcionFiltro(
                        opcion = opcion,
                        seleccionado = seleccionadoId == opcion.id,
                        onClick = { onSeleccionar(opcion.id) })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar)
            {
                Text(text = "Cerrar")
            }
        })
}

@Composable
private fun FilaOpcionFiltro(
    opcion: OpcionFiltro,
    seleccionado: Boolean,
    onClick: () -> Unit)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp))
    {
        RadioButton(
            selected = seleccionado,
            onClick = null)

        Text(
            text = opcion.nombre,
            style = MaterialTheme.typography.bodyMedium,
            color = if (opcion.activa)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = if (opcion.activa)
                null
            else
                TextDecoration.LineThrough,
            modifier = Modifier.weight(1f))

        if (!opcion.activa)
        {
            Text(
                text = "Inactiva",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ResumenGastos(
    resumen: ResumenMes?,
    pagedData: PagedData<Gasto>?,
    filtros: HistorialFiltrosUi,
    modifier: Modifier = Modifier)
{
    val pagina = pagedData?.page

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
    {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp))
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp))
            {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f),
                            shape = RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center)
                {
                    Icon(
                        imageVector = Icons.Rounded.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(21.dp))
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp))
                {
                    Text(
                        text = formatearMonto(resumen?.totalVigente ?: 0.0),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary)

                    Text(
                        text = "${pagina?.totalElements ?: 0} resultados · " +
                                "${resumen?.cantidadAnulados ?: 0} anulados " +
                                "en ${formatearMes(filtros.anio, filtros.mes)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (pagina != null && pagina.totalElements > 0)
            {
                LinearProgressIndicator(
                    progress = {
                        (pagedData?.items?.size ?: 0).toFloat() /
                                pagina.totalElements.toFloat()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            }

            Text(
                text = "Desliza un gasto hacia la derecha para anularlo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ErrorCargaGastos(
    mensaje: String,
    onReintentar: () -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = CircleShape),
            contentAlignment = Alignment.Center)
        {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No pudimos cargar los gastos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onReintentar,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.height(50.dp))
        {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp))

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Reintentar",
                style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ContenidoGastos(
    pagedData: PagedData<Gasto>,
    filtros: HistorialFiltrosUi,
    anulandoId: Long?,
    onGastoClick: (Long) -> Unit,
    onAnularSolicitado: (Gasto) -> Unit,
    onCargarMas: () -> Unit)
{
    val hayFiltros = filtros.cuentaId != null ||
            filtros.categoriaId != null ||
            filtros.etiquetaId != null ||
            filtros.comercioId != null ||
            filtros.descripcion.isNotBlank()

    if (pagedData.items.isEmpty())
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape),
                contentAlignment = Alignment.Center)
            {
                Icon(
                    imageVector = Icons.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hayFiltros)
                    "No hay gastos con esos filtros"
                else
                    "No hay gastos este mes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (hayFiltros)
                    "Prueba quitando algún filtro."
                else
                    "Cuando registres uno, va a aparecer acá.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
        }
    }
    else
    {
        val gastosAgrupados = remember(pagedData.items) {
            pagedData.items.groupBy { claveDia(it.fechaHora) }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp))
        {
            gastosAgrupados.forEach { (dia, gastosDelDia) ->
                item(key = "header-$dia") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.background
                                    .copy(alpha = 0.96f))
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape))

                        Text(
                            text = formatearEncabezadoDia(dia),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }

                items(
                    items = gastosDelDia,
                    key = { it.id }) { gasto ->
                    GastoItem(
                        gasto = gasto,
                        anulando = anulandoId == gasto.id,
                        onClick = { onGastoClick(gasto.id) },
                        onAnularSolicitado = { onAnularSolicitado(gasto) })
                }
            }

            item(key = "footer") {
                FooterPaginacion(
                    hayMas = pagedData.hayMas,
                    cargandoMas = pagedData.loadingMore,
                    error = pagedData.loadMoreError,
                    onCargarMas = onCargarMas)
            }
        }
    }
}

@Composable
private fun GastoItem(
    gasto: Gasto,
    anulando: Boolean,
    onClick: () -> Unit,
    onAnularSolicitado: () -> Unit)
{
    val anulado = gasto.estado == EstadoGasto.ANULADO
    val decoracion = if (anulado) TextDecoration.LineThrough else null
    val umbral = with(LocalDensity.current) { 96.dp.toPx() }
    var desplazamiento by remember(gasto.id, gasto.estado) {
        mutableFloatStateOf(0f)
    }

    Box(modifier = Modifier.fillMaxWidth())
    {
        if (desplazamiento > 0f)
        {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp))
            {
                Icon(
                    imageVector = Icons.Rounded.Block,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp))

                Text(
                    text = "Suelta para anular",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }

        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(desplazamiento.roundToInt(), 0) }
                .pointerInput(gasto.id, anulado, anulando) {
                    if (anulado || anulando)
                        return@pointerInput

                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (desplazamiento >= umbral)
                                onAnularSolicitado()

                            desplazamiento = 0f
                        },
                        onDragCancel = {
                            desplazamiento = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            desplazamiento = (desplazamiento + dragAmount)
                                .coerceIn(0f, umbral * 1.4f)
                        })
                },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)))
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically)
            {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center)
                {
                    Icon(
                        imageVector = Icons.Rounded.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(21.dp))
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp))
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        Text(
                            text = gasto.descripcion,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (anulado)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = decoracion,
                            modifier = Modifier.weight(1f))

                        if (anulado)
                        {
                            EstadoGastoBadge(estado = gasto.estado)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp))
                    {
                        Text(
                            text = formatearFechaHora(gasto.fechaHora),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)

                        gasto.categorias.firstOrNull()?.let { categoria ->
                            Text(
                                text = "·",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Text(
                                text = categoria.nombre,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f))
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp))
                {
                    Text(
                        text = formatearMonto(gasto.monto),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (anulado)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.primary,
                        textDecoration = decoracion)

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FooterPaginacion(
    hayMas: Boolean,
    cargandoMas: Boolean,
    error: String?,
    onCargarMas: () -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        error?.let { mensaje ->
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error)
        }

        if (hayMas)
        {
            OutlinedButton(
                onClick = onCargarMas,
                enabled = !cargandoMas,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp))
            {
                if (cargandoMas)
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary)
                }
                else
                {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp))

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (error != null) "Reintentar" else "Cargar más",
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        else
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center)
            {
                Icon(
                    imageVector = Icons.Rounded.CloudDone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp))

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Todos los gastos están cargados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun opcionesPara(
    tipo: TipoFiltro,
    opciones: OpcionesHistorialUi): List<OpcionFiltro> = when (tipo)
{
    TipoFiltro.CUENTA -> opciones.cuentas.map {
        OpcionFiltro(it.id, it.nombre, it.activa)
    }

    TipoFiltro.CATEGORIA -> opciones.categorias.map {
        OpcionFiltro(it.id, it.nombre, it.activa)
    }

    TipoFiltro.ETIQUETA -> opciones.etiquetas.map {
        OpcionFiltro(it.id, it.nombre, it.activa)
    }

    TipoFiltro.COMERCIO -> opciones.comercios.map {
        OpcionFiltro(it.id, it.nombre)
    }
}

private fun idSeleccionado(
    tipo: TipoFiltro,
    filtros: HistorialFiltrosUi): Long? = when (tipo)
{
    TipoFiltro.CUENTA -> filtros.cuentaId
    TipoFiltro.CATEGORIA -> filtros.categoriaId
    TipoFiltro.ETIQUETA -> filtros.etiquetaId
    TipoFiltro.COMERCIO -> filtros.comercioId
}

private fun tituloFiltro(tipo: TipoFiltro): String = when (tipo)
{
    TipoFiltro.CUENTA -> "Filtrar por cuenta"
    TipoFiltro.CATEGORIA -> "Filtrar por categoría"
    TipoFiltro.ETIQUETA -> "Filtrar por etiqueta"
    TipoFiltro.COMERCIO -> "Filtrar por comercio"
}

private fun textoTodos(tipo: TipoFiltro): String = when (tipo)
{
    TipoFiltro.CUENTA,
    TipoFiltro.COMERCIO -> "Todos"

    TipoFiltro.CATEGORIA,
    TipoFiltro.ETIQUETA -> "Todas"
}
