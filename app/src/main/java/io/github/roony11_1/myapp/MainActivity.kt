package io.github.roony11_1.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.roony11_1.myapp.ui.components.FondoPremium
import io.github.roony11_1.myapp.ui.navigation.BarraNavegacionInferior
import io.github.roony11_1.myapp.ui.navigation.Catalogos
import io.github.roony11_1.myapp.ui.navigation.Inicio
import io.github.roony11_1.myapp.ui.navigation.RegistrarGasto
import io.github.roony11_1.myapp.ui.navigation.VerGasto
import io.github.roony11_1.myapp.ui.navigation.VerGastos
import io.github.roony11_1.myapp.ui.navigation.catalogosEntry
import io.github.roony11_1.myapp.ui.navigation.mainScreenEntry
import io.github.roony11_1.myapp.ui.navigation.registrarGastoEntry
import io.github.roony11_1.myapp.ui.navigation.verGastoDetalleEntry
import io.github.roony11_1.myapp.ui.navigation.verGastosEntry
import io.github.roony11_1.myapp.ui.theme.MyAppTheme

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as MyApp).container

        setContent {
            MyAppTheme {
                FondoPremium {
                    val backStack = rememberNavBackStack(Inicio)
                    val rutaActual = backStack.lastOrNull()

                    val volverAInicio: () -> Unit = {
                        while (backStack.size > 1)
                            backStack.removeLastOrNull()
                    }

                    val navegarA: (NavKey) -> Unit = { destino ->
                        if (backStack.lastOrNull() != destino)
                        {
                            volverAInicio()

                            if (backStack.lastOrNull() != destino)
                                backStack.add(destino)
                        }
                    }

                    val onBack: () -> Unit = {
                        if (backStack.size > 1)
                            backStack.removeLastOrNull()
                        else
                            finish()
                    }

                    val mostrarBarra = rutaActual is Inicio ||
                            rutaActual is RegistrarGasto ||
                            rutaActual is VerGastos ||
                            rutaActual is Catalogos

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        bottomBar = {
                            if (mostrarBarra)
                            {
                                BarraNavegacionInferior(
                                    rutaActual = rutaActual,
                                    onInicioClick = volverAInicio,
                                    onRegistrarClick = { navegarA(RegistrarGasto) },
                                    onHistorialClick = { navegarA(VerGastos()) },
                                    onCatalogosClick = { navegarA(Catalogos) })
                            }
                        })
                    { innerPadding ->
                        NavDisplay(
                            backStack = backStack,
                            onBack = onBack,
                            modifier = Modifier.padding(innerPadding),
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()),
                            entryProvider = entryProvider
                            {
                                mainScreenEntry(
                                    container = container,
                                    onNavigateToRegistrarGasto = {
                                        navegarA(RegistrarGasto)
                                    },
                                    onNavigateToVerGastos = { anio, mes ->
                                        navegarA(VerGastos(anio, mes))
                                    },
                                    onNavigateToCatalogos = {
                                        navegarA(Catalogos)
                                    })

                                registrarGastoEntry(
                                    container = container,
                                    onGuardadoExitoso = volverAInicio,
                                    onBack = onBack)

                                verGastosEntry(
                                    container = container,
                                    onNavigateToGastoClick = { id ->
                                        backStack.add(VerGasto(id))
                                    },
                                    onBack = onBack)

                                verGastoDetalleEntry(
                                    container = container,
                                    onBack = onBack)

                                catalogosEntry(container = container)
                            })
                    }
                }
            }
        }
    }
}
