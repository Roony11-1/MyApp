package io.github.roony11_1.myapp.domain.usecase

import io.github.roony11_1.myapp.domain.model.Categoria
import io.github.roony11_1.myapp.domain.model.Cuenta
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class RegistrarGasto(
    private val gastoRepository: IGastoRepository)
{
    suspend operator fun invoke(
        monto: Double,
        descripcion: String,
        cuenta: Cuenta,
        categorias: List<Categoria>,
        fechaHora: Long): Gasto
    {
        require(monto > 0) { "El monto debe ser positivo" }
        require(descripcion.isNotBlank()) { "La descripción es obligatoria" }
        require(categorias.isNotEmpty()) { "Debe seleccionar una categoría" }

        val gasto = Gasto(
            monto = monto,
            descripcion = descripcion,
            cuenta = cuenta,
            categorias = categorias,
            fechaHora = fechaHora)

        return gastoRepository.save(gasto)
    }
}
