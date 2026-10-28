package io.github.roony11_1.myapp.core.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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

fun formatearFecha(epochMillis: Long): String
{
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val fecha = Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())

    return fecha.format(formatter)
}

fun formatearHora(epochMillis: Long): String
{
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val fecha = Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())

    return fecha.format(formatter)
}

fun claveDia(fechaHora: Long): LocalDate =
    Instant.ofEpochMilli(fechaHora)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

fun formatearMes(anio: Int, mes: Int): String
{
    val locale = Locale.forLanguageTag("es")
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
    val texto = YearMonth.of(anio, mes)
        .atDay(1)
        .format(formatter)

    return texto.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(locale) else it.toString()
    }
}

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