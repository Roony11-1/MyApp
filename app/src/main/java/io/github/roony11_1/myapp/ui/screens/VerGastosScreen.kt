package io.github.roony11_1.myapp.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    val cargandoMas by viewModel.cargandoMas.collectAsStateWithLifecycle()
    val hayMas by viewModel.hayMas.collectAsStateWithLifecycle()
    val errorAlCargarMas by
    viewModel.errorAlCargarMas.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargar() }

    Column(modifier = modifier.fillMaxSize())
    {
        Text(
            text = "Gastos",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(24.dp))

        when (val state = uiState) {
            is UiState.Idle -> Unit
            is UiState.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Text(
                text = state.mensaje,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(24.dp))
            is UiState.Exito -> {
                if (state.data.isEmpty())
                {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                    {
                        Text("No hay gastos registrados", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else
                {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        items(state.data, key = { it.id }) { gasto ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onGastoClick(gasto.id) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant))
                            {
                                Column(Modifier.padding(16.dp))
                                {
                                    Text(gasto.descripcion, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        formatearMonto(gasto.monto),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        item {
                            errorAlCargarMas?.let { mensaje ->
                                Text(
                                    text = mensaje,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
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
                                            color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                    else
                                    {
                                        Text(
                                            if (errorAlCargarMas != null)
                                                "Reintentar"
                                            else
                                                "Cargar más")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}