package io.github.roony11_1.myapp.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

fun fechaHoraAFechaLocal(epochMillis: Long): LocalDate =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

fun fechaHoraAHoraLocal(epochMillis: Long): LocalTime =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()

fun combinarFechaHora(fecha: LocalDate, hora: LocalTime): Long =
    fecha.atTime(hora)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

fun fechaLocalAUtcMillis(fecha: LocalDate): Long =
    fecha.atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

fun utcMillisAFechaLocal(utcMillis: Long): LocalDate =
    Instant.ofEpochMilli(utcMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
