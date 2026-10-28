package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.domain.model.ResumenMes
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class ObtenerResumenMes(
    private val gastoRepository: IGastoRepository)
{
    suspend operator fun invoke(
        desde: Long,
        hastaExclusivo: Long): ResumenMes =
        gastoRepository.resumenMes(desde, hastaExclusivo)
}
