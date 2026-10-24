package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
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

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    when (val state = uiState)
    {
        is UiState.Idle -> Unit
        is UiState.Cargando -> CircularProgressIndicator()
        is UiState.Error -> Text(state.mensaje, color = MaterialTheme.colorScheme.error)
        is UiState.Exito -> {
            if (state.data.isEmpty())
                Text("No hay gastos registrados")
            else
                LazyColumn(modifier = modifier.padding(16.dp))
                {
                    items(state.data) { gasto ->
                        Text(
                            text = "${gasto.descripcion} ${formatearMonto(gasto.monto)}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGastoClick(gasto.id) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
        }
    }
}