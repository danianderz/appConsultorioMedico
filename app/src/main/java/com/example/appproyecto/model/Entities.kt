package com.example.appproyecto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tblMedicos")
data class Medico (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="id")
    val id: String,
    @ColumnInfo(name="nombre")
    val nombre: String,
    @ColumnInfo(name="especialidad")
    val especialidad: String,
    @ColumnInfo(name="telefono")
    val telefono: String
): Serializable

@Entity(tableName = "tblPacientes")
data class Paciente (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="id")
    val id: String,
    @ColumnInfo(name="nombre")
    val nombre: String,
    @ColumnInfo(name="correo")
    val correo: String,
    @ColumnInfo(name="telefono")
    val telefono: String
): Serializable

@Entity(tableName = "tblConsultas")
data class Consulta (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    val id: Int,
    @ColumnInfo(name="idMedico")
    @ForeignKey(entity = Medico::class, parentColumns = ["id"], childColumns = ["idMedico"])
    val idMedico: String,
    @ColumnInfo(name="idPaciente")
    @ForeignKey(entity = Paciente::class, parentColumns = ["id"], childColumns = ["idPaciente"])
    val idPaciente: String,
    @ColumnInfo(name="NomMedico")
    val NomMedico: String,
    @ColumnInfo(name="NomPaciente")
    val NomPaciente: String,
    @ColumnInfo(name="Fecha")
    val fecha: String,
    @ColumnInfo(name="Hora")
    val hora: String,
    @ColumnInfo(name="Precio")
    val precio: Double
): Serializable
