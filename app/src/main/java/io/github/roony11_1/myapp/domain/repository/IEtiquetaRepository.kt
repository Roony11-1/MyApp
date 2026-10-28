package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.domain.model.Etiqueta

interface IEtiquetaRepository
{
    suspend fun findAll(): List<Etiqueta>
    suspend fun findActivas(): List<Etiqueta>
    suspend fun recomendar(query: String): List<Etiqueta>
    suspend fun crear(nombre: String): Etiqueta
    suspend fun cambiarActiva(id: Long, activa: Boolean): Boolean
}
