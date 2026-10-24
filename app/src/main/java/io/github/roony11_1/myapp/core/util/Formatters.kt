package io.github.roony11_1.myapp.core.util

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatearMonto(monto: Double): String
{
    return NumberFormat.getCurrencyInstance().format(monto)
}

fun formatearFechaHora(epochMillis: Long): String
{
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val fecha = Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())

    return fecha.format(formatter)
}