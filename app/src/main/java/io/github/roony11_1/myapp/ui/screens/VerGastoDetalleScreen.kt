package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.core.util.formatearFechaHora
import io.github.roony11_1.myapp.core.util.formatearMonto
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.ui.components.ChipPremium
import io.github.roony11_1.myapp.ui.components.EncabezadoPantalla
import io.github.roony11_1.myapp.ui.components.FilaDetallePremium
import io.github.roony11_1.myapp.ui.components.UiStateContent
import io.github.roony11_1.myapp.ui.viewmodel.VerGastoViewModel

@Composable
fun VerGastoDetalleScreen(
    modifier: Modifier = Modifier,
    viewModel: VerGastoViewModel,
    onBack: () -> Unit)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    Column(modifier = modifier.fillMaxSize())
    {
        EncabezadoPantalla(
            titulo = "Detalle del gasto",
            eyebrow = "Movimiento",
            subtitulo = "Todos los datos en un solo lugar.",
            onBack = onBack,
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 16.dp))

        UiStateContent<Gasto>(
            state = state,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            emptyContent = {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = CircleShape),
                        contentAlignment = Alignment.Center)
                    {
                        Icon(
                            imageVector = Icons.Rounded.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp))
                    }

                    Text(
                        text = "Gasto no encontrado",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp))

                    Text(
                        text = "Puede que el movimiento se haya eliminado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp))
                }
            })
        { gasto ->
            DetalleGasto(
                gasto = gasto,
                modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun DetalleGasto(gasto: Gasto, modifier: Modifier = Modifier)
{
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp))
    {
        Surface(
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
                        radius = size.minDimension * 0.6f,
                        center = Offset(size.width * 0.92f, size.height * 0.08f))

                    drawCircle(
                        color = onPrimary.copy(alpha = 0.06f),
                        radius = size.minDimension * 0.42f,
                        center = Offset(size.width * 0.04f, size.height * 0.96f))
                }

                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    Text(
                        text = "MONTO DEL GASTO",
                        style = MaterialTheme.typography.labelSmall,
                        color = onPrimary.copy(alpha = 0.72f))

                    Text(
                        text = formatearMonto(gasto.monto),
                        style = MaterialTheme.typography.headlineLarge,
                        color = onPrimary)

                    Text(
                        text = gasto.descripcion,
                        style = MaterialTheme.typography.titleMedium,
                        color = onPrimary.copy(alpha = 0.86f))
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
        {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp))
            {
                FilaDetallePremium(
                    icono = Icons.Rounded.AccountBalanceWallet,
                    etiqueta = "Cuenta",
                    valor = gasto.cuenta.nombre)

                gasto.comercio?.let { comercio ->
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.6f))

                    FilaDetallePremium(
                        icono = Icons.Rounded.Store,
                        etiqueta = "Comercio",
                        valor = comercio.nombre)
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                        .copy(alpha = 0.6f))

                FilaDetallePremium(
                    icono = Icons.Rounded.CalendarMonth,
                    etiqueta = "Fecha",
                    valor = formatearFechaHora(gasto.fechaHora))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                        .copy(alpha = 0.6f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp))
                {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center)
                    {
                        Icon(
                            imageVector = Icons.Rounded.Category,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(18.dp))
                    }

                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    gasto.categorias.forEach { categoria ->
                        EtiquetaCategoria(nombre = categoria.nombre)
                    }
                }

                if (gasto.etiquetas.isNotEmpty())
                {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.6f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp))
                    {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center)
                        {
                            Icon(
                                imageVector = Icons.Rounded.Label,
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = "Etiquetas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        gasto.etiquetas.forEach { etiqueta ->
                            ChipPremium(
                                texto = etiqueta.nombre,
                                seleccionado = true,
                                onClick = {},
                                enabled = false)
                        }
                    }
                }

                gasto.notasLimpia?.let { notas ->
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                            .copy(alpha = 0.6f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp))
                    {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center)
                        {
                            Icon(
                                imageVector = Icons.Rounded.Notes,
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = "Notas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text(
                        text = notas,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun EtiquetaCategoria(nombre: String)
{
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer)
    {
        Text(
            text = nombre,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
    }
}
