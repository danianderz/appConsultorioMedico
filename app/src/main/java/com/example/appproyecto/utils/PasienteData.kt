package com.example.appproyecto.utils

data class PasienteData(val id: String, val nombre: String) {
    override fun toString(): String {
        return "$id - $nombre"
    }
}