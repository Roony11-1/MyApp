package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.core.util.formatearFechaHora
import io.github.roony11_1.myapp.core.util.formatearMonto
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.ui.viewmodel.UiState
import io.github.roony11_1.myapp.ui.viewmodel.VerGastoViewModel

@Composable
fun VerGastoDetalleScreen(
    modifier: Modifier = Modifier,
    viewModel: VerGastoViewModel)
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    when (val state = uiState)
    {
        is UiState.Idle -> Unit
        is UiState.Cargando -> CircularProgressIndicator()
        is UiState.Error -> Text(state.mensaje, color = MaterialTheme.colorScheme.error)
        is UiState.Exito -> {
            val gasto = state.data
            if (gasto == null)
                Text("Gasto no encontrado")
            else
                DetalleGasto(gasto, modifier)
        }
    }
}

@Composable
private fun DetalleGasto(
    gasto: Gasto,
    modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp))
    {
        Text(text = formatearMonto(gasto.monto), style = MaterialTheme.typography.headlineMedium)

        Text(text = gasto.descripcion, style = MaterialTheme.typography.bodyLarge)

        Text(text = "Cuenta ID: ${gasto.cuentaId}", style = MaterialTheme.typography.bodyMedium)

        Text(text = "Categoría ID: ${gasto.categoriaId}", style = MaterialTheme.typography.bodyMedium)

        Text(
            text = formatearFechaHora(gasto.fechaHora),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}