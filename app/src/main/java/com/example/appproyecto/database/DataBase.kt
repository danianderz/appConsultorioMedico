package com.example.appproyecto.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appproyecto.model.Medico
import com.example.appproyecto.model.Paciente
import com.example.appproyecto.model.Consulta

@Database(entities = [Medico::class, Paciente::class, Consulta::class], version = 2, exportSchema = false)
abstract class DataBase:RoomDatabase(){
    //Definir el DAO a utilizar
    abstract fun medicoDao(): MedicoDao
    abstract fun pacienteDao(): PacienteDao
    abstract fun consultaDao(): ConsultaDao
    //Definir la instancia de la base de datos
    companion object{
        var instancia:DataBase? = null
        //Manejar la instancia de la base de datos
        fun getInstancia(contexto: Context):DataBase{
            if(instancia ==null){
                instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    DataBase::class.java,"bdConsultorio"
                ).addMigrations(MIGRATION_1_2)
                    .build()
            }
            return instancia as DataBase
        }
    }
}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tblMedicos RENAME TO tblMedicos_old")
        database.execSQL("CREATE TABLE tblMedicos (id TEXT PRIMARY KEY NOT NULL, nombre TEXT NOT NULL, especialidad TEXT NOT NULL, telefono TEXT NOT NULL)")
        database.execSQL("INSERT INTO tblMedicos (id, nombre, especialidad, telefono) SELECT id, nombre, especialidad, telefono FROM tblMedicos_old")
        database.execSQL("DROP TABLE tblMedicos_old")

        database.execSQL("ALTER TABLE tblPacientes RENAME TO tblPacientes_old")
        database.execSQL("CREATE TABLE tblPacientes (id TEXT PRIMARY KEY NOT NULL, nombre TEXT NOT NULL, correo TEXT NOT NULL, telefono TEXT NOT NULL)")
        database.execSQL("INSERT INTO tblPacientes (id, nombre, correo, telefono) SELECT id, nombre, correo, telefono FROM tblPacientes_old")
        database.execSQL("DROP TABLE tblPacientes_old")

        database.execSQL("ALTER TABLE tblConsultas RENAME TO tblConsultas_old")
        database.execSQL("CREATE TABLE tblConsultas (id INTEGER PRIMARY KEY NOT NULL, idMedico TEXT NOT NULL, idPaciente TEXT NOT NULL, NomMedico TEXT NOT NULL, NomPaciente TEXT NOT NULL, Fecha TEXT NOT NULL, Hora TEXT NOT NULL, Precio REAL NOT NULL)")
        database.execSQL("INSERT INTO tblConsultas (id, idMedico, idPaciente, NomMedico, NomPaciente, Fecha, Hora, Precio) SELECT id, idMedico, idPaciente, (SELECT nombre FROM tblMedicos WHERE id = idMedico) AS NomMedico, (SELECT nombre FROM tblPacientes WHERE id = idPaciente) AS NomPaciente, Fecha, Hora, Precio FROM tblConsultas_old")
        database.execSQL("DROP TABLE tblConsultas_old")
    }
}