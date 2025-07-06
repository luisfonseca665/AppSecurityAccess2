package com.example.login2

data class Acceso(
    val nombre: String,
    val fechaEntrada: String,
    val fechaSalida: String?,
    val autorizado: Boolean
)
