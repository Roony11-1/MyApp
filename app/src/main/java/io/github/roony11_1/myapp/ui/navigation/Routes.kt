package io.github.roony11_1.myapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object  Inicio: NavKey

@Serializable
data object RegistrarGasto: NavKey

@Serializable
data object VerGastos: NavKey

@Serializable
data class VerGasto(val id: Long): NavKey