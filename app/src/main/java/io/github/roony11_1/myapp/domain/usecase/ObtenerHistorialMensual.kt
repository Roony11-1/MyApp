package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.domain.model.MesHistorial
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class ObtenerHistorialMensual(
    private val gastoRepository: IGastoRepository)
{
    suspend operator fun invoke(
        desde: Long?,
        hastaExclusivo: Long): List<MesHistorial> =
        gastoRepository.historialMeses(desde, hastaExclusivo)
}
