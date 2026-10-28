
package io.github.roony11_1.myapp.data.local.repository

import io.github.roony11_1.myapp.core.pagination.Page
import io.github.roony11_1.myapp.data.local.dao.IGastoDao
import io.github.roony11_1.myapp.domain.model.Gasto
import io.github.roony11_1.myapp.domain.model.GastosFiltros
import io.github.roony11_1.myapp.domain.model.MesHistorial
import io.github.roony11_1.myapp.domain.model.ResumenMes
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
            notas = gasto.notas,
            estado = gasto.estado)

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
            notas = paraGuardar.notas,
            estado = paraGuardar.estado)
    }

    override suspend fun findAll(): List<Gasto> = dao.selectAll()

    override suspend fun findById(id: Long): Gasto? = dao.selectById(id)

    override suspend fun findPage(
        filtros: GastosFiltros,
        number: Int,
        size: Int): Page<Gasto> = dao.selectPage(filtros, number, size)

    override suspend fun buscarSimilares(
        descripcion: String,
        limite: Int): List<Gasto> = dao.buscarSimilares(descripcion, limite)

    override suspend fun anular(id: Long): Boolean = dao.anular(id)

    override suspend fun resumenMes(
        desde: Long,
        hastaExclusivo: Long): ResumenMes = dao.resumenMes(desde, hastaExclusivo)

    override suspend fun historialMeses(
        desde: Long?,
        hastaExclusivo: Long): List<MesHistorial> =
        dao.historialMeses(desde, hastaExclusivo)
}
