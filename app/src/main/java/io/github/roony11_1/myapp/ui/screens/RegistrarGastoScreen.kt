package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.ui.components.SelectorFechaHora
import io.github.roony11_1.myapp.ui.viewmodel.RegistrarGastoViewModel

@Composable
fun RegistrarGastoScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrarGastoViewModel,
    onGuardadoExitoso: () -> Unit)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val error = state.error

    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var categoriasSeleccionadas by remember {
        mutableStateOf(setOf<Long>(3L))
    }

    var cuentaSeleccionadaId by remember {
        mutableStateOf<Long>(1L)
    }

    LaunchedEffect(state.gastoGuardado) {
        if (state.gastoGuardado != null)
        {
            viewModel.onExitoConsumido()
            onGuardadoExitoso()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp))
    {
        Text(
            text = "Registrar gasto",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer)
        {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp))
            {
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal),
                    isError = error != null,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true)

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    isError = error != null,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer)
        {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp))
            {
                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface)

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    state.categorias.forEach { categoria ->
                        FilterChip(
                            selected = categoria.id in categoriasSeleccionadas,
                            onClick = {
                                categoriasSeleccionadas =
                                    if (categoria.id in categoriasSeleccionadas)
                                        categoriasSeleccionadas - categoria.id
                                    else
                                        categoriasSeleccionadas + categoria.id
                            },
                            label = {
                                Text(
                                    text = categoria.nombre,
                                    style = MaterialTheme.typography.labelLarge)
                            },
                            shape = MaterialTheme.shapes.small,
                            enabled = !state.ocupado)
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer)
        {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp))
            {
                Text(
                    text = "Cuenta",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface)

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    state.cuentas.forEach { cuenta ->
                        FilterChip(
                            selected = cuenta.id == cuentaSeleccionadaId,
                            onClick = {
                                cuentaSeleccionadaId = cuenta.id
                            },
                            label = {
                                Text(
                                    text = cuenta.nombre,
                                    style = MaterialTheme.typography.labelLarge)
                            },
                            shape = MaterialTheme.shapes.small,
                            enabled = !state.ocupado)
                    }
                }
            }
        }

        SelectorFechaHora(
            fechaHora = state.fechaHora,
            onFechaHoraChange = viewModel::onFechaHoraChange,
            enabled = !state.ocupado)

        if (error != null)
        {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.errorContainer)
            {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp))
            }
        }

        Button(
            onClick = {
                val montoDouble = monto.toDoubleOrNull()
                if (montoDouble == null || montoDouble <= 0)
                    return@Button

                val cuentaGuardar = state.cuentas.find {
                    it.id == cuentaSeleccionadaId
                } ?: return@Button

                val categoriasGuardar = state.categorias.filter {
                    it.id in categoriasSeleccionadas
                }

                viewModel.onGuardar(
                    monto = montoDouble,
                    descripcion = descripcion,
                    cuenta = cuentaGuardar,
                    categorias = categoriasGuardar,
                    fechaHora = state.fechaHora)
            },
            enabled = !state.ocupado,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp))
        {
            if (state.guardando)
            {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
            else
            {
                Text(
                    text = "Guardar",
                    style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
