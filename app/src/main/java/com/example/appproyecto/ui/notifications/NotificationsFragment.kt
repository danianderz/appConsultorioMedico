package com.example.appproyecto.ui.notifications

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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appproyecto.*
import com.example.appproyecto.database.DataBase
import com.example.appproyecto.databinding.FragmentHomeBinding
import com.example.appproyecto.databinding.FragmentNotificationsBinding
import com.example.appproyecto.model.Consulta
import com.example.appproyecto.model.Medico
import com.example.appproyecto.ui.dashboard.DashboardFragment
import com.example.appproyecto.utils.Constants
import org.json.JSONArray
import org.json.JSONException
import java.util.concurrent.Executors

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val adapter: ConsultorioAdapter by lazy{
        ConsultorioAdapter()
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
        val filter = IntentFilter(DashboardFragment.REFRESH_ACTION)
        requireContext().registerReceiver(refreshReceiver, filter)
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
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
            val intent = Intent(requireContext(), IngresoC::class.java).apply {
                putExtras(bundle)
            }
            startActivity(intent)
        }
        adapter.setOnClickListenerMedicoDelete = {
            val queue = Volley.newRequestQueue(context)
            val url = "http://10.79.7.33/consultorio/eliminarC.php"

            val postRequest = object : StringRequest(Method.POST, url, Response.Listener<String> { response ->
                Toast.makeText(activity, "Consulta eliminada", Toast.LENGTH_SHORT).show()
                cargarDatos()
            }, Response.ErrorListener { error ->
                Toast.makeText(activity, "Error al eliminar la consulta", Toast.LENGTH_SHORT).show()
            }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["id"] = it.id.toString()
                    return params
                }
            }
            queue.add(postRequest)
        }
    }
    fun cargarDatos(){
        val queue = Volley.newRequestQueue(activity)
        val url = "http://10.79.7.33/consultorio/accederC.php"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val viajes = parseJson(response)
                adapter.medicos = viajes
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Log.e("Volley error", error.toString())
                Toast.makeText(activity, "Error al cargar las consultas", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)
    }
    private fun parseJson(json: String): List<Consulta> {
        val viajes = mutableListOf<Consulta>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getInt("id")
                val idMedico = jsonObject.getString("idMedico")
                val idPaciente = jsonObject.getString("idPaciente")
                val NomMedico = jsonObject.getString("NomMedico")
                val NomPaciente = jsonObject.getString("NomPaciente")
                val fecha = jsonObject.getString("fecha")
                val hora = jsonObject.getString("hora")
                val precio = jsonObject.getDouble("precio")
                val viaje = Consulta(id,idMedico,idPaciente, NomMedico,NomPaciente,fecha,hora,precio)
                viajes.add(viaje)
            }
        } catch (e: JSONException) {
            Log.e("JSON parse error", e.toString())
        }
        return viajes
    }
    override fun onDestroyView() {
        requireContext().unregisterReceiver(refreshReceiver)
        super.onDestroyView()
        _binding = null
    }
    private fun eventos(){
        binding.flbRegistrarP.setOnClickListener(){
            startActivity(Intent(requireContext(), IngresoC::class.java))
        }
    }
}