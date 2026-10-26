package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.ui.components.BotonPrincipal
import io.github.roony11_1.myapp.ui.components.CampoTextoPremium
import io.github.roony11_1.myapp.ui.components.ChipPremium
import io.github.roony11_1.myapp.ui.components.EncabezadoPantalla
import io.github.roony11_1.myapp.ui.components.SelectorFechaHora
import io.github.roony11_1.myapp.ui.components.TarjetaSeccion
import io.github.roony11_1.myapp.ui.viewmodel.RegistrarGastoViewModel

@Composable
fun RegistrarGastoScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrarGastoViewModel,
    onGuardadoExitoso: () -> Unit,
    onBack: () -> Unit)
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
        EncabezadoPantalla(
            titulo = "Registrar gasto",
            eyebrow = "Nuevo movimiento",
            subtitulo = "Completa los datos y guarda en segundos.",
            onBack = onBack)

        TarjetaSeccion(
            titulo = "Detalle",
            icono = Icons.Rounded.Notes,
            descripcion = "Lo esencial del movimiento")
        {
            CampoTextoPremium(
                value = monto,
                onValueChange = { monto = it },
                label = "Monto",
                leadingIcon = Icons.Rounded.Payments,
                placeholder = "0,00",
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal),
                isError = error != null)

            CampoTextoPremium(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = "Descripción",
                leadingIcon = Icons.Rounded.Notes,
                placeholder = "¿En qué se fue?",
                minLines = 3,
                isError = error != null)
        }

        TarjetaSeccion(
            titulo = "Categorías",
            icono = Icons.Rounded.Category,
            descripcion = "Puedes elegir más de una")
        {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                state.categorias.forEach { categoria ->
                    ChipPremium(
                        texto = categoria.nombre,
                        seleccionado = categoria.id in categoriasSeleccionadas,
                        onClick = {
                            categoriasSeleccionadas =
                                if (categoria.id in categoriasSeleccionadas)
                                    categoriasSeleccionadas - categoria.id
                                else
                                    categoriasSeleccionadas + categoria.id
                        },
                        enabled = !state.ocupado)
                }
            }
        }

        TarjetaSeccion(
            titulo = "Cuenta",
            icono = Icons.Rounded.AccountBalanceWallet,
            descripcion = "¿De dónde salió el dinero?")
        {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                state.cuentas.forEach { cuenta ->
                    ChipPremium(
                        texto = cuenta.nombre,
                        seleccionado = cuenta.id == cuentaSeleccionadaId,
                        onClick = { cuentaSeleccionadaId = cuenta.id },
                        enabled = !state.ocupado)
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
                color = MaterialTheme.colorScheme.errorContainer,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.25f)))
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(18.dp))

                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f))
                }
            }
        }

        BotonPrincipal(
            texto = "Guardar gasto",
            icono = Icons.Rounded.Check,
            cargando = state.guardando,
            enabled = !state.ocupado,
            onClick = {
                val montoDouble = monto.toDoubleOrNull()
                val cuentaGuardar = state.cuentas.find {
                    it.id == cuentaSeleccionadaId
                }

                if (montoDouble != null && montoDouble > 0 && cuentaGuardar != null)
                {
                    val categoriasGuardar = state.categorias.filter {
                        it.id in categoriasSeleccionadas
                    }

                    viewModel.onGuardar(
                        monto = montoDouble,
                        descripcion = descripcion,
                        cuenta = cuentaGuardar,
                        categorias = categoriasGuardar,
                        fechaHora = state.fechaHora)
                }
            })
    }
}
