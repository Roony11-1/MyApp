package io.github.roony11_1.myapp.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.ui.screens.RegistrarGastoScreen
import io.github.roony11_1.myapp.ui.viewmodel.RegistrarGastoViewModel

fun EntryProviderScope<NavKey>.registrarGastoEntry(
    container: AppContainer,
    onGuardadoExitoso: () -> Unit)
{
    entry<RegistrarGasto>
    {
        val viewModel: RegistrarGastoViewModel = viewModel(
            factory = RegistrarGastoViewModel.factory(container))

        RegistrarGastoScreen(
            viewModel = viewModel,
            onGuardadoExitoso = onGuardadoExitoso)
    }
}