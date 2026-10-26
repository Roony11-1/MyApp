
package io.github.roony11_1.myapp.data.local.repository

import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.data.local.dao.IGastoDao
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import io.github.roony11_1.myapp.domain.repository.IGastoRepository

class GastoRepository(private val dao: IGastoDao) : IGastoRepository
{
    override suspend fun save(gasto: Gasto): Gasto
    {
        val etiquetas = gasto.etiquetas
            .distinctBy { normalizarNombre(it.nombre) }

        val paraGuardar = Gasto(
            monto = gasto.monto,
            descripcion = gasto.descripcion,
            cuenta = gasto.cuenta,
            categorias = gasto.categorias,
            fechaHora = gasto.fechaHora,
            comercio = gasto.comercio,
            etiquetas = etiquetas,
            notas = gasto.notas)

        val id = dao.insert(paraGuardar)

        return Gasto(
            id = id,
            monto = paraGuardar.monto,
            descripcion = paraGuardar.descripcion,
            cuenta = paraGuardar.cuenta,
            categorias = paraGuardar.categorias,
            fechaHora = paraGuardar.fechaHora,
            comercio = paraGuardar.comercio,
            etiquetas = paraGuardar.etiquetas,
            notas = paraGuardar.notas)
    }

    override suspend fun findAll(): List<Gasto> = dao.selectAll()

    override suspend fun findById(id: Long): Gasto? = dao.selectById(id)

    override suspend fun findPage(number: Int, size: Int): Page<Gasto> = dao.selectPage(number, size)
}
