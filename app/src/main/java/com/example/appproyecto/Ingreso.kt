package com.example.appproyecto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appproyecto.database.DataBase
import com.example.appproyecto.databinding.ActivityIngresoBinding
import com.example.appproyecto.model.Medico
import com.example.appproyecto.model.Paciente
import com.example.appproyecto.ui.dashboard.DashboardFragment
import com.example.appproyecto.ui.home.HomeFragment
import com.example.appproyecto.utils.Constants
import org.json.JSONException
import java.util.concurrent.Executors

class Ingreso : AppCompatActivity() {
    private lateinit var binding: ActivityIngresoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngresoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inicializar()
        eventos()
    }

    fun inicializar(){
        val bundle = intent.extras
        bundle?.let{
            val paciente = bundle.getSerializable(Constants.KEY_PACIENTE) as Medico
            binding.btnAgregar.text = "Actualizar Médico"
            binding.txtIngresar.text = "Actualización de Médico"
            binding.edtCedula.isEnabled = false
            binding.edtCedula.setText(paciente.id)
            binding.edtNombre.setText(paciente.nombre)
            binding.edtEspecialidad.setText(paciente.especialidad)
            binding.edtTelefono.setText(paciente.telefono)
            binding.edtNombre.requestFocus()
        }?:kotlin.run{
            binding.btnAgregar.text = "Agregar Médico"
            binding.txtIngresar.text = "Ingreso de Médico"
            binding.edtCedula.setText("")
            binding.edtNombre.setText("")
            binding.edtEspecialidad.setText("")
            binding.edtTelefono.setText("")
            binding.edtCedula.requestFocus()
        }

    }

    fun eventos(){
        binding.btnAgregar.setOnClickListener {
            val nombre = binding.edtNombre.text.toString()
            val correo = binding.edtEspecialidad.text.toString()
            val telefono = binding.edtTelefono.text.toString()
            val id = binding.edtCedula.text.toString()
            if (TextUtils.isEmpty(id)) {
                binding.edtCedula.error = "Ingrese la cedula del medico"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(nombre)) {
                binding.edtNombre.error = "Ingrese el nombre del médico"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(correo)) {
                binding.edtEspecialidad.error = "Ingrese la especialidad del médico"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(telefono)) {
                binding.edtTelefono.error = "Ingrese el teléfono del médico"
                return@setOnClickListener
            }
            if(binding.btnAgregar.text == "Agregar Médico"){
                agregar(Medico(id,nombre,correo,telefono))
                onBackPressed()
            }else{
                editar(Medico(id,nombre,correo,telefono))
                onBackPressed()
            }
        }
    }
    fun agregar(viaje: Medico) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.79.7.33/consultorio/guardarM.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    Toast.makeText(this, "Médico agregado con éxito", Toast.LENGTH_SHORT).show()
                    val refreshIntent = Intent(HomeFragment.REFRESH_ACTION)
                    sendBroadcast(refreshIntent)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al parsear la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = viaje.id
                params["nombre"] = viaje.nombre
                params["especialidad"] = viaje.especialidad
                params["telefono"] = viaje.telefono
                return params
            }
        }
        queue.add(stringRequest)
    }

    fun editar(paciente: Medico){
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.79.7.33/consultorio/editarM.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    Toast.makeText(this, "Médico editado con éxito", Toast.LENGTH_SHORT).show()
                    val refreshIntent = Intent(HomeFragment.REFRESH_ACTION)
                    sendBroadcast(refreshIntent)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al parsear la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = paciente.id
                params["nombre"] = paciente.nombre
                params["especialidad"] = paciente.especialidad
                params["telefono"] = paciente.telefono
                return params
            }
        }
        queue.add(stringRequest)
    }
}