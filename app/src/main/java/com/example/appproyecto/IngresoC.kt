package com.example.appproyecto

import android.R
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appproyecto.database.DataBase
import com.example.appproyecto.databinding.ActivityIngresoCBinding
import com.example.appproyecto.model.Consulta
import com.example.appproyecto.model.Medico
import com.example.appproyecto.model.Paciente
import com.example.appproyecto.ui.home.HomeFragment
import com.example.appproyecto.utils.Constants
import com.example.appproyecto.utils.MedicoData
import com.example.appproyecto.utils.PasienteData
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class IngresoC : AppCompatActivity(){
    private var id = 0
    private lateinit var binding: ActivityIngresoCBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngresoCBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val horas = arrayListOf<String>()
        for (hora in 8..20) {
            horas.add("$hora:00")
        }
        // Asigna las horas al spinner
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, horas)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerHora.adapter = adapter

        val queue = Volley.newRequestQueue(this)
        val url = "http://10.79.7.33/consultorio/acceder.php"
        val url2 = "http://10.79.7.33/consultorio/accederM.php"
        inicializar()
        val pacientesRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val pacientes = parsePacientesResponse(response)
                val pacientesDatos = pacientes.map { PasienteData(it.id, it.nombre) }
                val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, pacientesDatos)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spinnerUsuario.adapter = adapter
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error al cargar los pacientes", Toast.LENGTH_SHORT).show()
            }
        )

        val medicosRequest = StringRequest(Request.Method.GET, url2,
            Response.Listener<String> { response ->
                val medicos = parseMedicosResponse(response)
                val medicosDatos = medicos.map { MedicoData(it.id, it.nombre) }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medicosDatos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerMedico.adapter = adapter
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error al cargar los medicos", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(pacientesRequest)
        queue.add(medicosRequest)
        incializarComp()
        eventos()
    }
    fun inicializar(){
        val bundle = intent.extras
        bundle?.let{
            val paciente = bundle.getSerializable(Constants.KEY_PACIENTE) as Consulta
            binding.btnAgregar.text = "Actualizar Consulta"
            binding.txtIngresar.text = "Actualización de Consulta"
            id = paciente.id
            binding.edtFecha.setText(paciente.fecha)
            binding.edtPrecio.setText(paciente.precio.toString())
            var position = 0
            if (binding.spinnerUsuario.adapter != null) {
                for (i in 0 until binding.spinnerUsuario.adapter.count) {
                    val item = binding.spinnerUsuario.adapter.getItem(i) as PasienteData
                    if (item.id == paciente.idPaciente) {
                        position = i
                        break
                    }
                }
            }
            binding.spinnerUsuario.setSelection(position)
            binding.spinnerUsuario.isEnabled = false
        }?:kotlin.run{
            binding.btnAgregar.text = "Agregar Consulta"
            binding.txtIngresar.text = "Ingreso de Consulta"
            binding.edtFecha.setText("")
            binding.edtPrecio.setText("")
        }
    }
    fun eventos(){
        binding.btnAgregar.setOnClickListener {
            val idMedico = (binding.spinnerMedico.selectedItem as MedicoData).id
            val nombreMedico = (binding.spinnerMedico.selectedItem as MedicoData).nombre
            val idPaciente = (binding.spinnerUsuario.selectedItem as PasienteData).id
            val nombrePaciente = (binding.spinnerUsuario.selectedItem as PasienteData).nombre
            val fecha = binding.edtFecha.text.toString()
            val hora = binding.spinnerHora.selectedItem.toString()
            var precioString = binding.edtPrecio.text.toString()
            if(TextUtils.isEmpty(precioString)){
                precioString = "0"
            }
            val precio = precioString.toDouble()
            if (precio == null) {
                binding.edtPrecio.error = "El precio debe ser un número decimal"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(fecha)) {
                binding.edtFecha.error = "Ingrese la fecha"
                return@setOnClickListener
            }
            if (precioString =="0") {
                binding.edtPrecio.error = "Ingrese el precio"
                return@setOnClickListener
            }
            if(id==0){
                agregar(Consulta(0,idMedico,idPaciente,nombreMedico,nombrePaciente,fecha,hora,precio))
                onBackPressed()
            }else{
                editar(Consulta(id,idMedico,idPaciente,nombreMedico,nombrePaciente,fecha,hora,precio))
                onBackPressed()
            }
        }
    }
    fun agregar(consulta: Consulta){
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.79.7.33/consultorio/guardarC.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    Toast.makeText(this, "Consulta agregado con éxito", Toast.LENGTH_SHORT).show()
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
                params["id"] = consulta.id.toString()
                params["idMedico"] = consulta.idMedico
                params["idPaciente"] = consulta.idPaciente
                params["NomMedico"] = consulta.NomMedico
                params["NomPaciente"] = consulta.NomPaciente
                params["fecha"] = consulta.fecha
                params["hora"] = consulta.hora
                params["precio"] = consulta.precio.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    fun parsePacientesResponse(json: String): List<Paciente>{
        val viajes = mutableListOf<Paciente>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val nombre = jsonObject.getString("nombre")
                val correo = jsonObject.getString("correo")
                val telefono = jsonObject.getString("telefono")
                val viaje = Paciente(id,nombre,correo, telefono)
                viajes.add(viaje)
            }
        } catch (e: JSONException) {
            Log.e("JSON parse error", e.toString())
        }
        return viajes
    }

    fun parseMedicosResponse(json: String): List<Medico> {
        val viajes = mutableListOf<Medico>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val nombre = jsonObject.getString("nombre")
                val correo = jsonObject.getString("especialidad")
                val telefono = jsonObject.getString("telefono")
                val viaje = Medico(id,nombre,correo, telefono)
                viajes.add(viaje)
            }
        } catch (e: JSONException) {
            Log.e("JSON parse error", e.toString())
        }
        return viajes
    }
    fun editar(consulta: Consulta){
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.79.7.33/consultorio/editarC.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    Toast.makeText(this, "Consulta editada con éxito", Toast.LENGTH_SHORT).show()
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
                params["id"] = consulta.id.toString()
                params["idMedico"] = consulta.idMedico
                params["idPaciente"] = consulta.idPaciente
                params["NomMedico"] = consulta.NomMedico
                params["NomPaciente"] = consulta.NomPaciente
                params["fecha"] = consulta.fecha
                params["hora"] = consulta.hora
                params["precio"] = consulta.precio.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }
    private fun incializarComp(){
        binding.btnFecha.setOnClickListener{
            setOnClickListenerFecha()
        }
    }
    private fun setOnClickListenerFecha() {
        val datePickerFragment = DatePickerFragment { year, month, day ->
            binding.edtFecha.text = Editable.Factory.getInstance().newEditable("$day-$month-$year")
        }
        datePickerFragment.show(supportFragmentManager, "datePicker")
    }

    class DatePickerFragment (val listener: (year:Int , month:Int, day:Int) -> Unit):DialogFragment(), DatePickerDialog.OnDateSetListener{
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireActivity(), this, year, month, day)
            datePickerDialog.datePicker.minDate = c.timeInMillis
            return datePickerDialog
        }
            override fun onDateSet (p0: DatePicker?, year: Int, month: Int, day: Int) {
                listener (year, month+1, day)

            }
        }
}
