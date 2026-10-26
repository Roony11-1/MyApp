package io.github.roony11_1.myapp.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.roony11_1.myapp.core.di.AppContainer
import io.github.roony11_1.myapp.ui.screens.VerGastoDetalleScreen
import io.github.roony11_1.myapp.ui.viewmodel.VerGastoViewModel

fun EntryProviderScope<NavKey>.verGastoDetalleEntry(
    container: AppContainer,
    onBack: () -> Unit)
{
    entry<VerGasto> { key ->
        val viewModel: VerGastoViewModel = viewModel(
            factory = VerGastoViewModel.factory(container, key.id))

        VerGastoDetalleScreen(
            viewModel = viewModel,
            onBack = onBack)
    }
}