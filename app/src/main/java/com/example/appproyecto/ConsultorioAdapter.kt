package com.example.appproyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appproyecto.databinding.ItemConsultaBinding
import com.example.appproyecto.databinding.ItemMedicoBinding
import com.example.appproyecto.model.Consulta
import com.example.appproyecto.model.Medico

class ConsultorioAdapter(var medicos: List<Consulta> =
                        emptyList()): RecyclerView.Adapter<ConsultorioAdapter.PetAdapterViewHolder>() {
    lateinit var setOnClickListenerMedicoEdit:(Consulta) -> Unit
    lateinit var setOnClickListenerMedicoDelete:(Consulta) -> Unit
    private var listener: OnMovieClickListener? = null

    inner class PetAdapterViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        private var binding: ItemConsultaBinding = ItemConsultaBinding.bind(itemView)
        fun bind(consulta: Consulta){
            binding.txtMedico.text = consulta.NomPaciente
            binding.txtPaciente.text = consulta.NomMedico
            binding.txtFecha.text = consulta.fecha
            binding.txtHora.text = consulta.hora
            binding.txtPrecio.text = consulta.precio.toString()
            binding.btnEditar.setOnClickListener{
                setOnClickListenerMedicoEdit(consulta)
            }
            binding.btnEliminar.setOnClickListener{
                setOnClickListenerMedicoDelete(consulta)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_consulta,parent,false)
        return PetAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetAdapterViewHolder, position: Int) {
        val medico = medicos[position]
        holder.bind(medico)
    }

    override fun getItemCount(): Int {
        return medicos.size
    }

    fun updateListPets(medicos:List<Consulta>){
        this.medicos = medicos
        notifyDataSetChanged()
    }
    interface OnMovieClickListener {
        fun onMovieClick(medico: Consulta)
    }
}