package io.github.roony11_1.myapp.domain.repository

import io.github.roony11_1.myapp.domain.model.Etiqueta

interface IEtiquetaRepository
{
    suspend fun recomendar(query: String): List<Etiqueta>
    suspend fun crear(nombre: String): Etiqueta
}
