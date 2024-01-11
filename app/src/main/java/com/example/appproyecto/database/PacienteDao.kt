package com.example.appproyecto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appproyecto.model.Medico
import com.example.appproyecto.model.Paciente

@Dao
interface PacienteDao {
    //Sirve para definir el CRUD
    @Insert
    fun insert(movie: Paciente):Long
    @Update
    fun update(movie: Paciente)
    @Delete
    fun delete(movie: Paciente)
    @Query("select * from tblPacientes order by id")
    fun getPacientes(): LiveData<List<Paciente>>
    @Query("select * from tblPacientes where id=:idInput")
    fun getPacienteById(idInput:String):List<Paciente>
}