package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.data.local.dao.ICategoriaDao
import io.github.roony11_1.myapp.domain.model.Categoria

interface ICategoriaRepository
{
    suspend fun findAll(): List<Categoria>
    suspend fun findActivas(): List<Categoria>
    suspend fun crear(nombre: String): Categoria
    suspend fun cambiarActiva(id: Long, activa: Boolean): Boolean
}

class CategoriaRepository(
    private val dao: ICategoriaDao) : ICategoriaRepository
{
    override suspend fun findAll(): List<Categoria> =
        dao.selectAll()

    override suspend fun findActivas(): List<Categoria> =
        dao.selectActivas()

    override suspend fun crear(nombre: String): Categoria =
        dao.crear(nombre)

    override suspend fun cambiarActiva(
        id: Long,
        activa: Boolean): Boolean = dao.cambiarActiva(id, activa)
}
