package io.github.roony11_1.myapp.domain.model

data class GastosFiltros(
    val desde: Long,
    val hastaExclusivo: Long,
    val cuentaId: Long? = null,
    val categoriaId: Long? = null,
    val etiquetaId: Long? = null,
    val comercioId: Long? = null,
    val descripcion: String = "")
