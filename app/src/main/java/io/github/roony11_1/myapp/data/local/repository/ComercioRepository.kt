package io.github.roony11_1.myapp.data.local.repository

import io.github.roony11_1.myapp.data.local.dao.IComercioDao
import io.github.roony11_1.myapp.domain.model.Comercio
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import io.github.roony11_1.myapp.domain.repository.IComercioRepository

class ComercioRepository(
    private val dao: IComercioDao) : IComercioRepository
{
    override suspend fun findAll(): List<Comercio> = dao.selectAll()

    override suspend fun recomendar(query: String): List<Comercio>
    {
        val normalizada = normalizarNombre(query)
        if (normalizada.isEmpty())
            return emptyList()

        return dao.recomendar(normalizada)
    }

    override suspend fun crear(nombre: String): Comercio =
        dao.crear(nombre)
}
