package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.domain.model.Comercio

interface IComercioRepository
{
    suspend fun findAll(): List<Comercio>
    suspend fun recomendar(query: String): List<Comercio>
    suspend fun crear(nombre: String): Comercio
}
