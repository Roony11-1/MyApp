package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class VerGasto(
    private val gastoRepository: IGastoRepository)
{
    suspend operator fun invoke(id: Long): Gasto? = gastoRepository.findById(id)
}