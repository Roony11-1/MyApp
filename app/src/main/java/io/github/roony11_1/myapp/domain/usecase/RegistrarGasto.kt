package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.core.services.IClock
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class RegistrarGasto(
    private val gastoRepository: IGastoRepository,
    private val clock: IClock)
{
    suspend operator fun invoke(
        monto: Double,
        descripcion: String,
        cuentaId: Long,
        categoriaId: Long): Gasto
    {
        require(monto > 0) { "El monto debe ser positivo" }
        require(descripcion.isNotBlank()) { "La descripción es obligatoria" }
        require(cuentaId > 0) { "Debe seleccionar una cuenta" }
        require(categoriaId > 0) { "Debe seleccionar una categoría" }

        val gasto = Gasto(
            monto = monto,
            descripcion = descripcion,
            cuentaId = cuentaId,
            categoriaId = categoriaId,
            fechaHora = clock.now())

        return gastoRepository.save(gasto)
    }
}