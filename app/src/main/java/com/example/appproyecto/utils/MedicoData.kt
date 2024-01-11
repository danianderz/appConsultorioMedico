package com.example.appproyecto.utils

data class MedicoData(val id: String, val nombre: String) {
    override fun toString(): String {
        return "$id - $nombre"
    }
}