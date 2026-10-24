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

data class Gasto(
    val id: Long = 0L,
    val monto: Double,
    val descripcion: String,
    val cuentaId: Long,
    val categoriaId: Long,
    val fechaHora: Long)
{
    init
    {
        require(monto > 0) { "El monto debe ser positivo" }
        require(descripcion.isNotBlank()) { "La descripción es obligatorio" }
        require(cuentaId > 0) { "La cuenta es obligatoria" }
        require(categoriaId > 0) { "La categoría es obligatoria" }
    }
}