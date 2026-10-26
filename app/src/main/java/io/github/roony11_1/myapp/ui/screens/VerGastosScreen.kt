package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.core.util.claveDia
import io.github.roony11_1.myapp.core.util.formatearEncabezadoDia
import io.github.roony11_1.myapp.core.util.formatearFechaHora
import io.github.roony11_1.myapp.core.util.formatearMonto
import io.github.roony11_1.myapp.ui.viewmodel.VerGastosViewModel

@Composable
fun VerGastosScreen(
    modifier: Modifier = Modifier,
    viewModel: VerGastosViewModel,
    onGastoClick: (Long) -> Unit)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    Column(
        modifier = modifier.fillMaxSize())
    {
        Text(
            text = "Gastos",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 12.dp))

        val pagina = state.pagina
        val error = state.error

        when
        {
            state.cargandoInicial -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center)
                {
                    CircularProgressIndicator()
                }
            }

            error != null && state.gastos.isEmpty() -> {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp))
                {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error)

                    Button(
                        onClick = { viewModel.cargar() },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.height(48.dp))
                    {
                        Text(
                            text = "Reintentar",
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            pagina != null -> {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer)
                {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp))
                    {
                        Text(
                            text = "${state.gastos.size} de " +
                                    "${pagina.totalElements} gastos cargados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface)

                        if (pagina.totalPages > 0)
                        {
                            Text(
                                text = "Páginas cargadas: " +
                                        "${pagina.number + 1} de ${pagina.totalPages}" +
                                        " · ${pagina.size} por página",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (state.sinResultados)
                {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center)
                    {
                        Text(
                            text = "No hay gastos registrados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else
                {
                    // La BD ya entrega ordenado por fecha_hora DESC,
                    // y groupBy preserva el orden de aparición de las claves.
                    val gastosAgrupados = remember(state.gastos) {
                        state.gastos.groupBy { claveDia(it.fechaHora) }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp))
                    {
                        gastosAgrupados.forEach { (dia, gastosDelDia) ->

                            stickyHeader(key = "header-$dia") {
                                Text(
                                    text = formatearEncabezadoDia(dia),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.background)
                                        .padding(
                                            start = 4.dp,
                                            end = 4.dp,
                                            top = 12.dp,
                                            bottom = 8.dp))
                            }

                            items(
                                items = gastosDelDia,
                                key = { it.id }) { gasto ->
                                Card(
                                    onClick = { onGastoClick(gasto.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(
                                        containerColor =
                                            MaterialTheme.colorScheme.surfaceContainerLow),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 0.dp),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color =
                                            MaterialTheme.colorScheme.outlineVariant))
                                {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = 20.dp,
                                                vertical = 16.dp),
                                        horizontalArrangement =
                                            Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.Top)
                                    {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement =
                                                Arrangement.spacedBy(6.dp))
                                        {
                                            Text(
                                                text = gasto.descripcion,
                                                style = MaterialTheme.typography
                                                    .titleMedium,
                                                color = MaterialTheme.colorScheme
                                                    .onSurface)

                                            Text(
                                                text =
                                                    formatearFechaHora(gasto.fechaHora),
                                                style =
                                                    MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme
                                                    .onSurfaceVariant)
                                        }

                                        Text(
                                            text = formatearMonto(gasto.monto),
                                            style = MaterialTheme.typography
                                                .titleLarge,
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        item(key = "footer") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp))
                            {
                                error?.let { mensaje ->
                                    Text(
                                        text = mensaje,
                                        style =
                                            MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error)
                                }

                                if (state.hayMas)
                                {
                                    Button(
                                        onClick = { viewModel.cargarMas() },
                                        enabled = !state.cargandoMas,
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp))
                                    {
                                        if (state.cargandoMas)
                                        {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp,
                                                color =
                                                    MaterialTheme.colorScheme
                                                        .onPrimary)
                                        }
                                        else
                                        {
                                            Text(
                                                text =
                                                    if (error != null)
                                                        "Reintentar"
                                                    else
                                                        "Cargar más",
                                                style = MaterialTheme.typography
                                                    .labelLarge)
                                        }
                                    }
                                }
                                else
                                {
                                    Text(
                                        text = "Todos los gastos están cargados",
                                        style =
                                            MaterialTheme.typography.bodySmall,
                                        color =
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(vertical = 16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
