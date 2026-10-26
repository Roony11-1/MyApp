package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.core.util.claveDia
import io.github.roony11_1.myapp.core.util.formatearEncabezadoDia
import io.github.roony11_1.myapp.core.util.formatearFechaHora
import io.github.roony11_1.myapp.core.util.formatearMonto
import io.github.roony11_1.myapp.domain.model.EstadoGasto
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.ui.components.EncabezadoPantalla
import io.github.roony11_1.myapp.ui.components.EstadoGastoBadge
import io.github.roony11_1.myapp.ui.components.UiStateContent
import io.github.roony11_1.myapp.ui.viewmodel.PagedData
import io.github.roony11_1.myapp.ui.viewmodel.VerGastosViewModel

@Composable
fun VerGastosScreen(
    modifier: Modifier = Modifier,
    viewModel: VerGastosViewModel,
    onGastoClick: (Long) -> Unit,
    onBack: () -> Unit)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.recargar()

        onPauseOrDispose { }
    }

    Column(modifier = modifier.fillMaxSize())
    {
        EncabezadoPantalla(
            titulo = "Mis gastos",
            eyebrow = "Historial",
            subtitulo = "Todo lo que registraste, en orden.",
            onBack = onBack,
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 16.dp))

        UiStateContent<PagedData<Gasto>>(
            state = state,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            errorContent = { mensaje ->
                ErrorCargaGastos(
                    mensaje = mensaje,
                    onReintentar = viewModel::cargar)
            })
        { pagedData ->
            ContenidoGastos(
                pagedData = pagedData,
                onGastoClick = onGastoClick,
                onCargarMas = viewModel::cargarMas)
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
    onGastoClick: (Long) -> Unit,
    onCargarMas: () -> Unit)
{
    val pagina = pagedData.page

    Column(modifier = Modifier.fillMaxSize())
    {
        ResumenGastos(
            pagedData = pagedData,
            modifier = Modifier.padding(horizontal = 20.dp))

        if (pagedData.items.isEmpty())
        {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                    text = "Aún no hay gastos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Cuando registres uno, va a aparecer acá.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center)
            }
        }
        else
        {
            // La BD ya entrega ordenado por fecha_hora DESC,
            // y groupBy preserva el orden de aparición de las claves.
            val gastosAgrupados = remember(pagedData.items) {
                pagedData.items.groupBy { claveDia(it.fechaHora) }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 20.dp,
                    vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                gastosAgrupados.forEach { (dia, gastosDelDia) ->

                    stickyHeader(key = "header-$dia") {
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
                            onClick = { onGastoClick(gasto.id) })
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
}

@Composable
private fun ResumenGastos(
    pagedData: PagedData<Gasto>,
    modifier: Modifier = Modifier)
{
    val pagina = pagedData.page

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
    {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp))
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

                Column(verticalArrangement = Arrangement.spacedBy(2.dp))
                {
                    Text(
                        text = "${pagedData.items.size} de " +
                                "${pagina.totalElements} gastos",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface)

                    if (pagina.totalPages > 0)
                    {
                        Text(
                            text = "Página ${pagina.number + 1} de " +
                                    "${pagina.totalPages} · ${pagina.size} por página",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (pagina.totalElements > 0)
            {
                LinearProgressIndicator(
                    progress = {
                        pagedData.items.size.toFloat() /
                                pagina.totalElements.toFloat()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            }
        }
    }
}

@Composable
private fun GastoItem(
    gasto: Gasto,
    onClick: () -> Unit)
{
    val anulado = gasto.estado == EstadoGasto.ANULADO
    val decoracion = if (anulado) TextDecoration.LineThrough else null

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
