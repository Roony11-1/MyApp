package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.ui.viewmodel.RegistrarGastoViewModel
import io.github.roony11_1.myapp.ui.viewmodel.UiState

@Composable
fun RegistrarGastoScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrarGastoViewModel,
    onGuardadoExitoso: () -> Unit)
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Exito)
            onGuardadoExitoso()
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp))
    {
        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") })

        when (val state = uiState)
        {
            is UiState.Error -> Text(state.mensaje, color = MaterialTheme.colorScheme.error)
            UiState.Cargando -> CircularProgressIndicator()
            else -> Unit
        }

        Button(
            onClick = {
                val montoDouble = monto.toDoubleOrNull()
                if (montoDouble == null || montoDouble <= 0)
                    return@Button

                viewModel.onGuardar(
                    monto = montoDouble,
                    descripcion = descripcion,
                    cuentaId = 1L,
                    categoriaId = 1L)
            },
            enabled = uiState !is UiState.Cargando)
        {
            Text("Guardar")
        }
    }
}