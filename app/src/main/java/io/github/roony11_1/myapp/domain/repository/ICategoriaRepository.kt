package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.data.local.dao.ICategoriaDao
import io.github.roony11_1.myapp.domain.model.Categoria

interface ICategoriaRepository
{
    suspend fun findAll(): List<Categoria>
}

class CategoriaRepository(
    private val dao: ICategoriaDao) : ICategoriaRepository
{
    override suspend fun findAll(): List<Categoria> =
        dao.selectAll()
}