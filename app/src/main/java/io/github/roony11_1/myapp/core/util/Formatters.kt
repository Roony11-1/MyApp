package io.github.roony11_1.myapp.core.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
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
fun claveDia(fechaHora: Long): LocalDate =
    Instant.ofEpochMilli(fechaHora)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

fun formatearEncabezadoDia(dia: LocalDate): String
{
    val hoy = LocalDate.now()
    val ayer = hoy.minusDays(1)
    return when (dia)
    {
        hoy -> "Hoy"
        ayer -> "Ayer"
        else -> dia.format(
            java.time.format.DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy")
        )
    }
}