package io.github.roony11_1.myapp.domain.repository

interface IPeriodoHistorialRepository
{
    fun obtenerMeses(): Int?
    fun guardarMeses(meses: Int?)
}
