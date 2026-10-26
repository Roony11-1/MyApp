package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.domain.model.EstadoGasto
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class AnularGasto(
    private val gastoRepository: IGastoRepository)
{
    suspend operator fun invoke(id: Long): Gasto
    {
        val actual = gastoRepository.findById(id)
            ?: throw IllegalArgumentException("Gasto no encontrado")

        if (actual.estado == EstadoGasto.ANULADO)
            throw IllegalStateException("El gasto ya está anulado")

        if (!gastoRepository.anular(id))
        {
            val despues = gastoRepository.findById(id)

            if (despues?.estado == EstadoGasto.ANULADO)
                throw IllegalStateException("El gasto ya está anulado")

            throw IllegalStateException("No se pudo anular el gasto")
        }

        return gastoRepository.findById(id)
            ?: throw IllegalStateException("No se pudo recargar el gasto")
    }
}
