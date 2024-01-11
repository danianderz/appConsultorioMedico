package com.example.appproyecto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appproyecto.model.Medico

@Dao
interface MedicoDao {
    //Sirve para definir el CRUD
    @Insert
    fun insert(movie: Medico):Long
    @Update
    fun update(movie: Medico)
    @Delete
    fun delete(movie: Medico)
    @Query("select * from tblMedicos order by id")
    fun getMedicos(): LiveData<List<Medico>>
    @Query("select * from tblMedicos where id=:idInput")
    fun getPacienteById(idInput:String):List<Medico>
}