package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.data.local.dao.ICuentaDao
import io.github.roony11_1.myapp.domain.model.Cuenta

interface ICuentaRepository
{
    suspend fun findAll(): List<Cuenta>
    suspend fun crear(nombre: String): Cuenta
}

class CuentaRepository(
    private val dao: ICuentaDao) : ICuentaRepository
{
    override suspend fun findAll(): List<Cuenta> =
        dao.selectAll()

    override suspend fun crear(nombre: String): Cuenta =
        dao.crear(nombre)
}
