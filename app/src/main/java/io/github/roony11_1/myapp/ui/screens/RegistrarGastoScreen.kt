package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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

    var categoriasSeleccionadas by remember {
        mutableStateOf(setOf<Long>(3L))
    }

    val categorias by viewModel.categorias.collectAsStateWithLifecycle()

    var cuentaSeleccionadaId by remember {
        mutableStateOf<Long>(1L)
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Exito)
        {
            viewModel.onExitoConsumido()
            onGuardadoExitoso()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp))
    {
        Text("Registrar gasto", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = uiState is UiState.Error,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true)

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            isError = uiState is UiState.Error,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2)

        Text(
            text = "Categorías",
            style = MaterialTheme.typography.labelLarge)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categorias.forEach { categoria ->
                FilterChip(
                    selected = categoria.id in categoriasSeleccionadas,
                    onClick = {
                        categoriasSeleccionadas =
                            if (categoria.id in categoriasSeleccionadas)
                                categoriasSeleccionadas - categoria.id
                            else
                                categoriasSeleccionadas + categoria.id
                    },
                    label = { Text(categoria.nombre) },
                    enabled = uiState !is UiState.Cargando
                )
            }
        }

        Text(
            text = "Cuenta",
            style = MaterialTheme.typography.labelLarge)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp))
        {
            viewModel.cuentas.forEach { cuenta ->
                FilterChip(
                    selected = cuenta.id == cuentaSeleccionadaId,
                    onClick = {
                        cuentaSeleccionadaId = cuenta.id
                    },
                    label = { Text(cuenta.nombre) },
                    enabled = uiState !is UiState.Cargando
                )
            }
        }

        if (uiState is UiState.Error)
        {
            Text(
                text = (uiState as UiState.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }



        Button(
            onClick = {
                val montoDouble = monto.toDoubleOrNull()
                if (montoDouble == null || montoDouble <= 0)
                    return@Button

                val cuentaGuardar = viewModel.cuentas.find {
                    it.id == cuentaSeleccionadaId
                } ?: return@Button

                val categoriasGuardar = categorias.filter {
                    it.id in categoriasSeleccionadas
                }

                viewModel.onGuardar(
                    montoDouble,
                    descripcion,
                    cuentaGuardar,
                    categoriasGuardar)
            },
            enabled = uiState !is UiState.Cargando,
            modifier = Modifier.fillMaxWidth())
        {
            if (uiState is UiState.Cargando)
            {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
            else
            {
                Text("Guardar")
            }
        }
    }
}