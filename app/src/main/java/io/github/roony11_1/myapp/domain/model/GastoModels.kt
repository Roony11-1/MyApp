package io.github.roony11_1.myapp.domain.model

data class Cuenta(
    val id: Long = 0L,
    val nombre: String)
{
    init
    {
        require(nombre.isNotBlank()) { "El nombre es obligatorio" }
    }
}

data class Categoria(
    val id: Long = 0L,
    val nombre: String)
{
    init
    {
        require(nombre.isNotBlank()) { "El nombre es obligatorio" }
    }
}

class Gasto(
    val id: Long = 0L,
    val monto: Double,
    val descripcion: String,
    val cuenta: Cuenta,
    categorias: List<Categoria>,
    val fechaHora: Long)
{
    val categorias: List<Categoria> = categorias
        .distinctBy { it.id }

    init
    {
        require(monto > 0) { "El monto debe ser positivo" }
        require(descripcion.isNotBlank()) { "La descripción es obligatoria" }
        require(this.categorias.isNotEmpty()) { "Debe seleccionar una categoría" }
    }
}