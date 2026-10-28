package io.github.roony11_1.myapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.NewLabel
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.roony11_1.myapp.ui.components.BotonAgregar
import io.github.roony11_1.myapp.ui.components.CampoTextoPremium
import io.github.roony11_1.myapp.ui.components.EncabezadoPantalla
import io.github.roony11_1.myapp.ui.components.TarjetaSeccion
import io.github.roony11_1.myapp.ui.viewmodel.CatalogosViewModel

private enum class TipoCatalogo
{
    CUENTA,
    CATEGORIA,
    ETIQUETA
}

private data class ElementoCatalogo(
    val tipo: TipoCatalogo,
    val id: Long,
    val nombre: String)

@Composable
fun CatalogosScreen(
    modifier: Modifier = Modifier,
    viewModel: CatalogosViewModel)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var elementoAConfirmar by remember { mutableStateOf<ElementoCatalogo?>(null) }
    var mostrarAgregarCategoria by remember { mutableStateOf(false) }
    var nuevaCategoria by remember { mutableStateOf("") }

    LifecycleResumeEffect(Unit) {
        viewModel.cargar()

        onPauseOrDispose { }
    }

    if (state.cargando && state.cuentas.isEmpty() &&
        state.categorias.isEmpty() && state.etiquetas.isEmpty())
    {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp)
        }
    }
    else
    {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp))
        {
            item(key = "encabezado") {
                EncabezadoPantalla(
                    titulo = "Catálogos",
                    eyebrow = "Configuración",
                    subtitulo = "Desactiva elementos para dejar de usarlos " +
                            "en gastos nuevos sin perder el historial.")
            }

            state.error?.let { mensaje ->
                item(key = "error") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.errorContainer)
                    {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            Text(
                                text = mensaje,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f))

                            TextButton(onClick = viewModel::cargar)
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp))

                                Text(text = "Reintentar")
                            }
                        }
                    }
                }
            }

            item(key = "cuentas") {
                TarjetaSeccion(
                    titulo = "Cuentas",
                    icono = Icons.Rounded.AccountBalanceWallet,
                    descripcion = "Origen del dinero")
                {
                    if (state.cuentas.isEmpty())
                    {
                        TextoSinElementos()
                    }
                    else
                    {
                        state.cuentas.forEachIndexed { index, cuenta ->
                            FilaCatalogo(
                                nombre = cuenta.nombre,
                                activa = cuenta.activa,
                                habilitado = !state.procesando,
                                onCambiar = {
                                    if (cuenta.activa)
                                    {
                                        elementoAConfirmar = ElementoCatalogo(
                                            tipo = TipoCatalogo.CUENTA,
                                            id = cuenta.id,
                                            nombre = cuenta.nombre)
                                    }
                                    else
                                    {
                                        viewModel.cambiarCuenta(cuenta.id, true)
                                    }
                                })

                            if (index < state.cuentas.lastIndex)
                            {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant
                                        .copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }

            item(key = "categorias") {
                TarjetaSeccion(
                    titulo = "Categorías",
                    icono = Icons.Rounded.Category,
                    descripcion = "Clasificación de los gastos",
                    accion = {
                        BotonAgregar(
                            onClick = { mostrarAgregarCategoria = true },
                            enabled = !state.procesando && !state.cargando,
                            contentDescription = "Agregar categoría")
                    })
                {
                    if (state.categorias.isEmpty())
                    {
                        TextoSinElementos()
                    }
                    else
                    {
                        state.categorias.forEachIndexed { index, categoria ->
                            FilaCatalogo(
                                nombre = categoria.nombre,
                                activa = categoria.activa,
                                habilitado = !state.procesando,
                                onCambiar = {
                                    if (categoria.activa)
                                    {
                                        elementoAConfirmar = ElementoCatalogo(
                                            tipo = TipoCatalogo.CATEGORIA,
                                            id = categoria.id,
                                            nombre = categoria.nombre)
                                    }
                                    else
                                    {
                                        viewModel.cambiarCategoria(categoria.id, true)
                                    }
                                })

                            if (index < state.categorias.lastIndex)
                            {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant
                                        .copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }

            item(key = "etiquetas") {
                TarjetaSeccion(
                    titulo = "Etiquetas",
                    icono = Icons.Rounded.NewLabel,
                    descripcion = "Detalles opcionales")
                {
                    if (state.etiquetas.isEmpty())
                    {
                        TextoSinElementos()
                    }
                    else
                    {
                        state.etiquetas.forEachIndexed { index, etiqueta ->
                            FilaCatalogo(
                                nombre = etiqueta.nombre,
                                activa = etiqueta.activa,
                                habilitado = !state.procesando,
                                onCambiar = {
                                    if (etiqueta.activa)
                                    {
                                        elementoAConfirmar = ElementoCatalogo(
                                            tipo = TipoCatalogo.ETIQUETA,
                                            id = etiqueta.id,
                                            nombre = etiqueta.nombre)
                                    }
                                    else
                                    {
                                        viewModel.cambiarEtiqueta(etiqueta.id, true)
                                    }
                                })

                            if (index < state.etiquetas.lastIndex)
                            {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant
                                        .copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarAgregarCategoria)
    {
        AlertDialog(
            onDismissRequest = {
                if (!state.procesando)
                {
                    mostrarAgregarCategoria = false
                    nuevaCategoria = ""
                }
            },
            title = {
                Text(text = "Agregar categoría")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp))
                {
                    CampoTextoPremium(
                        value = nuevaCategoria,
                        onValueChange = { nuevaCategoria = it },
                        label = "Nombre",
                        leadingIcon = Icons.Rounded.Category,
                        placeholder = "Ej. Comida rápida",
                        singleLine = true)

                    Text(
                        text = "Si ya existe una categoría inactiva con ese " +
                                "nombre, se reactivará.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.agregarCategoria(nuevaCategoria)
                        mostrarAgregarCategoria = false
                        nuevaCategoria = ""
                    },
                    enabled = nuevaCategoria.isNotBlank() &&
                            !state.procesando &&
                            !state.cargando)
                {
                    Text(text = "Agregar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarAgregarCategoria = false
                        nuevaCategoria = ""
                    },
                    enabled = !state.procesando)
                {
                    Text(text = "Cancelar")
                }
            })
    }

    elementoAConfirmar?.let { elemento ->
        AlertDialog(
            onDismissRequest = {
                if (!state.procesando)
                    elementoAConfirmar = null
            },
            title = {
                Text(text = "Desactivar ${nombreTipo(elemento.tipo)}")
            },
            text = {
                Text(
                    text = "\"${elemento.nombre}\" dejará de aparecer en " +
                            "nuevos gastos. Los movimientos históricos " +
                            "conservarán su referencia.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (elemento.tipo)
                        {
                            TipoCatalogo.CUENTA ->
                                viewModel.cambiarCuenta(elemento.id, false)

                            TipoCatalogo.CATEGORIA ->
                                viewModel.cambiarCategoria(elemento.id, false)

                            TipoCatalogo.ETIQUETA ->
                                viewModel.cambiarEtiqueta(elemento.id, false)
                        }

                        elementoAConfirmar = null
                    },
                    enabled = !state.procesando,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error))
                {
                    Text(text = "Desactivar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { elementoAConfirmar = null },
                    enabled = !state.procesando)
                {
                    Text(text = "Cancelar")
                }
            })
    }
}

@Composable
private fun FilaCatalogo(
    nombre: String,
    activa: Boolean,
    habilitado: Boolean,
    onCambiar: () -> Unit)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp))
    {
        Text(
            text = nombre,
            style = MaterialTheme.typography.bodyMedium,
            color = if (activa)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = if (activa) null else TextDecoration.LineThrough,
            modifier = Modifier.weight(1f))

        BadgeEstadoCatalogo(activa = activa)

        TextButton(
            onClick = onCambiar,
            enabled = habilitado,
            colors = ButtonDefaults.textButtonColors(
                contentColor = if (activa)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary))
        {
            Text(text = if (activa) "Desactivar" else "Reactivar")
        }
    }
}

@Composable
private fun BadgeEstadoCatalogo(activa: Boolean)
{
    Surface(
        shape = CircleShape,
        color = if (activa)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.surfaceContainerHigh)
    {
        Text(
            text = if (activa) "Activa" else "Inactiva",
            style = MaterialTheme.typography.labelSmall,
            color = if (activa)
                MaterialTheme.colorScheme.onSecondaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
private fun TextoSinElementos()
{
    Text(
        text = "No hay elementos todavía.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}

private fun nombreTipo(tipo: TipoCatalogo): String = when (tipo)
{
    TipoCatalogo.CUENTA -> "cuenta"
    TipoCatalogo.CATEGORIA -> "categoría"
    TipoCatalogo.ETIQUETA -> "etiqueta"
}
