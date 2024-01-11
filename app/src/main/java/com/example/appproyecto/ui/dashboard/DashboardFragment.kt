package com.example.appproyecto.ui.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appproyecto.IngresoP
import com.example.appproyecto.PacienteAdapter
import com.example.appproyecto.database.DataBase
import com.example.appproyecto.databinding.FragmentDashboardBinding
import com.example.appproyecto.model.Paciente
import com.example.appproyecto.utils.Constants
import org.json.JSONArray
import org.json.JSONException
import java.util.concurrent.Executors

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val adapter: PacienteAdapter by lazy{
        PacienteAdapter()
    }
    companion object {
        const val REFRESH_ACTION = "REFRESH_ACTION"
    }

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            cargarDatos()
        }
    }
    private lateinit var rv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val filter = IntentFilter(REFRESH_ACTION)
        requireContext().registerReceiver(refreshReceiver, filter)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        rv = binding.rvPaciente
        cargarAdaptador()
        cargarDatos()
        setUpAdapter()
        eventos()
        return binding.root
    }
    private fun cargarAdaptador(){
        rv.adapter = adapter
    }
    private fun setUpAdapter(){
        rv.adapter = adapter
        adapter.setOnClickListenerMedicoEdit = {
            var bundle = Bundle().apply {
                putSerializable(Constants.KEY_PACIENTE, it)
            }
            val intent = Intent(requireContext(), IngresoP::class.java).apply {
                putExtras(bundle)
            }
            startActivity(intent)
        }
        adapter.setOnClickListenerMedicoDelete = {
            val queue = Volley.newRequestQueue(context)
            val url = "http://10.79.7.33/consultorio/eliminar.php"

            val postRequest = object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                Toast.makeText(activity, "Paciente eliminado", Toast.LENGTH_SHORT).show()
                cargarDatos()
            }, Response.ErrorListener { error ->
                Toast.makeText(activity, "Error al eliminar al paciente", Toast.LENGTH_SHORT).show()
            }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["id"] = it.id
                    return params
                }
            }
            queue.add(postRequest)
        }
    }
    fun cargarDatos(){
        val queue = Volley.newRequestQueue(activity)
        val url = "http://10.79.7.33/consultorio/acceder.php"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val viajes = parseJson(response)
                adapter.pasientes = viajes
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Log.e("Volley error", error.toString())
                Toast.makeText(activity, "Error al cargar los pacientes", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)
    }
    private fun parseJson(json: String): List<Paciente> {
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
    private fun eventos(){
        binding.flbRegistrarP.setOnClickListener(){
            startActivity(Intent(requireContext(), IngresoP::class.java))
        }
    }
    override fun onDestroyView() {
        requireContext().unregisterReceiver(refreshReceiver)
        super.onDestroyView()
        _binding = null
    }
}