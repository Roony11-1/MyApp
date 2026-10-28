package io.github.roony11_1.myapp.data.local.repository

import android.content.Context
import io.github.roony11_1.myapp.domain.repository.IPeriodoHistorialRepository

class PeriodoHistorialRepository(context: Context) : IPeriodoHistorialRepository
{
    private val preferencias = context.applicationContext
        .getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE)

    override fun obtenerMeses(): Int?
    {
        val valor = preferencias.getInt(CLAVE_PERIODO, PERIODO_POR_DEFECTO)

        return when
        {
            valor == PERIODO_TODO -> null
            valor in PERIODOS_VALIDOS -> valor
            else -> PERIODO_POR_DEFECTO
        }
    }

    override fun guardarMeses(meses: Int?)
    {
        val valor = if (meses == null || meses !in PERIODOS_VALIDOS)
            PERIODO_TODO
        else
            meses

        preferencias.edit()
            .putInt(CLAVE_PERIODO, valor)
            .apply()
    }

    companion object
    {
        const val PERIODO_POR_DEFECTO = 6
        private const val PERIODO_TODO = -1
        private const val NOMBRE_PREFERENCIAS = "preferencias"
        private const val CLAVE_PERIODO = "periodo_historial_meses"
        private val PERIODOS_VALIDOS = setOf(3, 6, 12)
    }
}
