package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.core.util.formatearMes
import io.github.roony11_1.myapp.core.util.formatearMonto
import io.github.roony11_1.myapp.domain.model.MesHistorial
import io.github.roony11_1.myapp.ui.components.TarjetaSeccion
import io.github.roony11_1.myapp.ui.viewmodel.HomeViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onVerGastosClick: (Int, Int) -> Unit,
    onRegistrarGastoClick: () -> Unit,
    onCatalogoClick: () -> Unit)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarPeriodo by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        viewModel.recargar()

        onPauseOrDispose { }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp))
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp))
        {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary)),
                        shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center)
            {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp))
            {
                Text(
                    text = "MyApp",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground)

                Text(
                    text = "Finanzas con claridad",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Surface(
                onClick = onCatalogoClick,
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                        .copy(alpha = 0.7f)))
            {
                Box(contentAlignment = Alignment.Center)
                {
                    Icon(
                        imageVector = Icons.Rounded.Category,
                        contentDescription = "Catálogos",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp))
                }
            }
        }

        AccesoRegistroRapido(onClick = onRegistrarGastoClick)

        TarjetaSeccion(
            titulo = "Cómo vamos este mes",
            icono = Icons.Rounded.CalendarMonth,
            descripcion = formatearMes(state.anioActual, state.mesActual))
        {
            val resumen = state.resumenActual

            if (state.cargando && resumen == null)
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    contentAlignment = Alignment.Center)
                {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp)
                }
            }
            else
            {
                Text(
                    text = formatearMonto(resumen?.totalVigente ?: 0.0),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary)

                Text(
                    text = "${resumen?.cantidadVigente ?: 0} gastos vigentes · " +
                            "${resumen?.cantidadAnulados ?: 0} anulados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Button(
                    onClick = {
                        onVerGastosClick(state.anioActual, state.mesActual)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium)
                {
                    Text(text = "Ver detalle del mes")
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically)
            {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp))
                {
                    Text(
                        text = "Historial mensual",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground)

                    Text(
                        text = etiquetaPeriodo(state.periodoMeses),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                TextButton(
                    onClick = { mostrarPeriodo = true })
                {
                    Text(text = "Período")
                }
            }

            if (state.cargando && state.meses.isEmpty())
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    contentAlignment = Alignment.Center)
                {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp)
                }
            }
            else if (state.meses.isEmpty())
            {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.7f)))
                {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp))
                    {
                        Text(
                            text = "Todavía no hay meses con gastos",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface)

                        Text(
                            text = "Cuando registres un gasto, aparecerá aquí " +
                                    "agrupado por mes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else
            {
                state.meses.forEach { mes ->
                    MesHistorialItem(
                        mes = mes,
                        onClick = { onVerGastosClick(mes.anio, mes.mes) })
                }
            }
        }

        state.error?.let { mensaje ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.errorContainer)
            {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f))

                    TextButton(onClick = viewModel::recargar)
                    {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp))

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(text = "Reintentar")
                    }
                }
            }
        }
    }

    if (mostrarPeriodo)
    {
        DialogoPeriodoHistorial(
            periodoActual = state.periodoMeses,
            onSeleccionar = { meses ->
                viewModel.cambiarPeriodo(meses)
                mostrarPeriodo = false
            },
            onCerrar = { mostrarPeriodo = false })
    }
}

@Composable
private fun AccesoRegistroRapido(onClick: () -> Unit)
{
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = Color.Transparent)
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(primary, secondary)),
                    shape = MaterialTheme.shapes.extraLarge))
        {
            Canvas(modifier = Modifier.matchParentSize())
            {
                drawCircle(
                    color = onPrimary.copy(alpha = 0.08f),
                    radius = size.minDimension * 0.55f,
                    center = Offset(size.width * 0.9f, size.height * 0.05f))

                drawCircle(
                    color = onPrimary.copy(alpha = 0.06f),
                    radius = size.minDimension * 0.4f,
                    center = Offset(size.width * 0.05f, size.height * 0.95f))
            }

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp))
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp))
                {
                    Icon(
                        imageVector = Icons.Rounded.AddCircle,
                        contentDescription = null,
                        tint = onPrimary,
                        modifier = Modifier.size(22.dp))

                    Text(
                        text = "REGISTRO RÁPIDO",
                        style = MaterialTheme.typography.labelSmall,
                        color = onPrimary.copy(alpha = 0.85f))
                }

                Text(
                    text = "Registrar gasto",
                    style = MaterialTheme.typography.headlineMedium,
                    color = onPrimary)

                Text(
                    text = "Anota un movimiento en segundos y sigue " +
                            "con lo tuyo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onPrimary.copy(alpha = 0.82f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp))
                {
                    Text(
                        text = "Empezar ahora",
                        style = MaterialTheme.typography.labelLarge,
                        color = onPrimary)

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = onPrimary,
                        modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun MesHistorialItem(
    mes: MesHistorial,
    onClick: () -> Unit)
{
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
    {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp))
        {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.13f),
                        shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center)
            {
                Icon(
                    imageVector = Icons.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(21.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp))
            {
                Text(
                    text = formatearMes(mes.anio, mes.mes),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface)

                Text(
                    text = "${mes.cantidadGastos - mes.cantidadAnulados} vigentes · " +
                            "${mes.cantidadAnulados} anulados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp))
            {
                Text(
                    text = formatearMonto(mes.totalVigente),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary)

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
private fun DialogoPeriodoHistorial(
    periodoActual: Int?,
    onSeleccionar: (Int?) -> Unit,
    onCerrar: () -> Unit)
{
    val opciones = listOf(
        3 to "Últimos 3 meses",
        6 to "Últimos 6 meses",
        12 to "Últimos 12 meses",
        null to "Todo el historial")

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(text = "Período del historial")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp))
            {
                Text(
                    text = "Elige cuántos meses se muestran habitualmente. " +
                            "Los gastos antiguos no se borran.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                opciones.forEach { (valor, etiqueta) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        RadioButton(
                            selected = periodoActual == valor,
                            onClick = { onSeleccionar(valor) })

                        Text(
                            text = etiqueta,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f))
                    }
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

private fun etiquetaPeriodo(meses: Int?): String = when (meses)
{
    null -> "Mostrando todo el historial"
    1 -> "Mostrando el último mes"
    else -> "Mostrando los últimos $meses meses"
}
