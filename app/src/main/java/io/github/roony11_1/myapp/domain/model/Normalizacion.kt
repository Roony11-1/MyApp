package io.github.roony11_1.myapp.domain.model

private val ESPACIOS = Regex("\\s+")

internal fun normalizarNombre(valor: String): String =
    valor.trim().lowercase().replace(ESPACIOS, " ")
