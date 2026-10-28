package io.github.roony11_1.myapp.domain.model

data class ResumenMes(
    val totalVigente: Double,
    val cantidadVigente: Long,
    val cantidadAnulados: Long)

data class MesHistorial(
    val anio: Int,
    val mes: Int,
    val totalVigente: Double,
    val cantidadGastos: Long,
    val cantidadAnulados: Long)
