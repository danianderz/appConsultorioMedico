package com.example.appproyecto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appproyecto.model.Consulta

@Dao
interface ConsultaDao {
    //Sirve para definir el CRUD
    @Insert
    fun insert(movie: Consulta):Long
    @Update
    fun update(movie: Consulta)
    @Delete
    fun delete(movie: Consulta)
    @Query("select * from tblConsultas order by id")
    fun getConsultas(): LiveData<List<Consulta>>
    @Query("select * from tblConsultas where id=:idInput")
    fun getConsultaById(idInput:Int):List<Consulta>
}