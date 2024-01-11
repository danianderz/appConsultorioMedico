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
import com.example.appproyecto.databinding.ActivityIngresoPBinding
import com.example.appproyecto.model.Paciente
import com.example.appproyecto.ui.dashboard.DashboardFragment
import com.example.appproyecto.utils.Constants
import org.json.JSONException
import java.util.concurrent.Executors

class IngresoP : AppCompatActivity() {
    private lateinit var binding: ActivityIngresoPBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngresoPBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inicializar()
        eventos()
    }


    fun inicializar(){
        val bundle = intent.extras
        bundle?.let{
            val paciente = bundle.getSerializable(Constants.KEY_PACIENTE) as Paciente
            binding.btnAgregar.text = "Actualizar paciente"
            binding.edtCedulaP.isEnabled = false
            binding.txtIngresar.text = "Actualización de Paciente"
            binding.edtCedulaP.setText(paciente.id)
            binding.edtNombre.setText(paciente.nombre)
            binding.edtEspecialidad.setText(paciente.correo)
            binding.edtTelefono.setText(paciente.telefono)
            binding.edtNombre.requestFocus()
        }?:kotlin.run{
            binding.btnAgregar.text = "Agregar paciente"
            binding.txtIngresar.text = "Ingreso de Paciente"
            binding.edtCedulaP.setText("")
            binding.edtNombre.setText("")
            binding.edtEspecialidad.setText("")
            binding.edtTelefono.setText("")
            binding.edtCedulaP.requestFocus()
        }

    }

    fun eventos(){
        binding.btnAgregar.setOnClickListener {
            val nombre = binding.edtNombre.text.toString()
            val correo = binding.edtEspecialidad.text.toString()
            val telefono = binding.edtTelefono.text.toString()
            val id = binding.edtCedulaP.text.toString()
            if (TextUtils.isEmpty(nombre)) {
                binding.edtCedulaP.error = "Ingrese la cedula del peciente"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(nombre)) {
                binding.edtNombre.error = "Ingrese el nombre del paciente"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(correo)) {
                binding.edtEspecialidad.error = "Ingrese el correo del paciente"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(telefono)) {
                binding.edtTelefono.error = "Ingrese el teléfono del paciente"
                return@setOnClickListener
            }
            if(binding.btnAgregar.text == "Agregar paciente"){
                agregar(Paciente(id,nombre,correo,telefono))
                onBackPressed()
            }else{
                editar(Paciente(id,nombre,correo,telefono))
                onBackPressed()
            }
        }
    }
    fun agregar(viaje: Paciente) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.79.7.33/consultorio/guardar.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    Toast.makeText(this, "Paciente agregado con éxito", Toast.LENGTH_SHORT).show()
                    val refreshIntent = Intent(DashboardFragment.REFRESH_ACTION)
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
                params["correo"] = viaje.correo
                params["telefono"] = viaje.telefono
                return params
            }
        }
        queue.add(stringRequest)
    }

    fun editar(paciente: Paciente){
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.79.7.33/consultorio/editar.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    Toast.makeText(this, "Paciente editado con éxito", Toast.LENGTH_SHORT).show()
                    val refreshIntent = Intent(DashboardFragment.REFRESH_ACTION)
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
                params["correo"] = paciente.correo
                params["telefono"] = paciente.telefono
                return params
            }
        }
        queue.add(stringRequest)
    }
}