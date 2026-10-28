package io.github.roony11_1.myapp.domain.model

data class Cuenta(
    val id: Long = 0L,
    val nombre: String,
    val activa: Boolean = true)
{
    init
    {
        require(nombre.isNotBlank()) { "El nombre es obligatorio" }
    }
}

data class Categoria(
    val id: Long = 0L,
    val nombre: String,
    val activa: Boolean = true)
{
    init
    {
        require(nombre.isNotBlank()) { "El nombre es obligatorio" }
    }
}

data class Comercio(
    val id: Long = 0L,
    val nombre: String)
{
    init
    {
        require(nombre.isNotBlank()) { "El nombre es obligatorio" }
    }
}

data class Etiqueta(
    val id: Long = 0L,
    val nombre: String,
    val activa: Boolean = true)
{
    init
    {
        require(nombre.isNotBlank()) { "La etiqueta no puede estar vacía" }
    }
}

enum class EstadoGasto
{
    VIGENTE,
    ANULADO
}

class Gasto(
    val id: Long = 0L,
    val monto: Double,
    val descripcion: String,
    val cuenta: Cuenta,
    categorias: List<Categoria>,
    val fechaHora: Long,
    val comercio: Comercio? = null,
    etiquetas: List<Etiqueta> = emptyList(),
    val notas: String? = null,
    val estado: EstadoGasto = EstadoGasto.VIGENTE)
{
    /** Categorías sin duplicados por id. */
    val categorias: List<Categoria> = categorias.distinctBy { it.id }

    /** Etiquetas sin duplicados por id. */
    val etiquetas: List<Etiqueta> = etiquetas.distinctBy { it.id }

    /** Notas normalizadas solo en extremos; el contenido se respeta. */
    val notasLimpia: String? = notas?.trim()?.takeIf { it.isNotEmpty() }

    init
    {
        require(monto > 0) { "El monto debe ser positivo" }
        require(descripcion.isNotBlank()) { "La descripción es obligatoria" }
        require(this.categorias.isNotEmpty()) { "Debe seleccionar una categoría" }
    }
}
