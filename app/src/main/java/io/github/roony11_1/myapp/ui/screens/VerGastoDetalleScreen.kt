package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.core.util.formatearFechaHora
import io.github.roony11_1.myapp.core.util.formatearMonto
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.ui.components.UiStateContent
import io.github.roony11_1.myapp.ui.viewmodel.VerGastoViewModel

@Composable
fun VerGastoDetalleScreen(
    modifier: Modifier = Modifier,
    viewModel: VerGastoViewModel)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    UiStateContent<Gasto>(
        state = state,
        modifier = modifier.fillMaxSize(),
        emptyContent = {
            Text(
                text = "Gasto no encontrado",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp))
        })
    { gasto ->
        DetalleGasto(
            gasto = gasto,
            modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun DetalleGasto(gasto: Gasto, modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp))
    {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 2.dp)
        {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp))
            {
                Text(
                    text = formatearMonto(gasto.monto),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary)

                Text(
                    text = gasto.descripcion,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                FilaDetalle("Cuenta", gasto.cuenta.nombre)
                FilaDetalle(
                    "Categorías",
                    gasto.categorias.joinToString(", ") { it.nombre })
                FilaDetalle("Fecha", formatearFechaHora(gasto.fechaHora))
            }
        }
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp))
    {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f))

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f))
    }
}
