package io.github.roony11_1.myapp.data.local.repository

import io.github.roony11_1.myapp.data.local.dao.IEtiquetaDao
import io.github.roony11_1.myapp.domain.model.Etiqueta
import io.github.roony11_1.myapp.domain.model.normalizarNombre
import io.github.roony11_1.myapp.domain.repository.IEtiquetaRepository

class EtiquetaRepository(
    private val dao: IEtiquetaDao) : IEtiquetaRepository
{
    override suspend fun findAll(): List<Etiqueta> = dao.selectAll()

    override suspend fun findActivas(): List<Etiqueta> = dao.selectActivas()

    override suspend fun recomendar(query: String): List<Etiqueta>
    {
        val normalizada = normalizarNombre(query)
        if (normalizada.isEmpty())
            return emptyList()

        return dao.recomendar(normalizada)
    }

    override suspend fun crear(nombre: String): Etiqueta =
        dao.crear(nombre)

    override suspend fun cambiarActiva(
        id: Long,
        activa: Boolean): Boolean = dao.cambiarActiva(id, activa)
}
