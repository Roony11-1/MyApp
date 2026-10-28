package io.github.roony11_1.myapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Inicio : NavKey

@Serializable
data object RegistrarGasto : NavKey

@Serializable
data class VerGastos(
    val anio: Int? = null,
    val mes: Int? = null) : NavKey

@Serializable
data object Catalogos : NavKey

@Serializable
data class VerGasto(val id: Long) : NavKey
