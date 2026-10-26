package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import io.github.roony11_1.myapp.ui.viewmodel.UiState
import io.github.roony11_1.myapp.ui.viewmodel.VerGastosViewModel



@Composable
fun VerGastosScreen(
    modifier: Modifier = Modifier,
    viewModel: VerGastosViewModel,
    onGastoClick: (Long) -> Unit)
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagina by viewModel.pagina.collectAsStateWithLifecycle()
    val cargandoMas by viewModel.cargandoMas.collectAsStateWithLifecycle()
    val hayMas by viewModel.hayMas.collectAsStateWithLifecycle()
    val errorAlCargarMas by
    viewModel.errorAlCargarMas.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    Column(
        modifier = modifier.fillMaxSize())
    {
        Text(
            text = "Gastos",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(24.dp))

        when (val state = uiState)
        {
            UiState.Idle -> Unit

            UiState.Cargando -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center)
                {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    Text(
                        text = state.mensaje,
                        color = MaterialTheme.colorScheme.error)

                    Button(
                        onClick = { viewModel.cargar() })
                    {
                        Text("Reintentar")
                    }
                }
            }

            is UiState.Exito -> {
                pagina?.let { page ->
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 24.dp,
                            vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp))
                    {
                        Text(
                            text = "${state.data.size} de " +
                                    "${page.totalElements} gastos cargados",
                            style = MaterialTheme.typography.bodyMedium)

                        if (page.totalPages > 0)
                        {
                            Text(
                                text = "Páginas cargadas: " +
                                        "${page.number + 1} de ${page.totalPages}" +
                                        " · ${page.size} por página",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (state.data.isEmpty())
                {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center)
                    {
                        Text(
                            text = "No hay gastos registrados",
                            style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else
                {
                    // La BD ya entrega ordenado por fecha_hora DESC,
                    // y groupBy preserva el orden de aparición de las claves.
                    val gastosAgrupados = remember(state.data) {
                        state.data.groupBy { claveDia(it.fechaHora) }
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
                                        .padding(vertical = 8.dp))
                            }

                            items(
                                items = gastosDelDia,
                                key = { it.id }) { gasto ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            onGastoClick(gasto.id)
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor =
                                            MaterialTheme.colorScheme.surfaceVariant))
                                {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp))
                                    {
                                        Text(
                                            text = gasto.descripcion,
                                            style = MaterialTheme.typography.titleMedium)

                                        Text(
                                            text = formatearMonto(gasto.monto),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary)

                                        Text(
                                            text = formatearFechaHora(gasto.fechaHora),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .align(Alignment.End)
                                                .padding(top = 8.dp))
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
                                errorAlCargarMas?.let { mensaje ->
                                    Text(
                                        text = mensaje,
                                        color = MaterialTheme.colorScheme.error)
                                }

                                if (hayMas)
                                {
                                    Button(
                                        onClick = { viewModel.cargarMas() },
                                        enabled = !cargandoMas,
                                        modifier = Modifier.fillMaxWidth())
                                    {
                                        if (cargandoMas)
                                        {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp,
                                                color =
                                                    MaterialTheme.colorScheme.onPrimary)
                                        }
                                        else
                                        {
                                            Text(
                                                text =
                                                    if (errorAlCargarMas != null)
                                                        "Reintentar"
                                                    else
                                                        "Cargar más")
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