package com.example.appproyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appproyecto.databinding.ItemMedicoBinding
import com.example.appproyecto.model.Medico

class MedicoAdapter(var medicos: List<Medico> =
                       emptyList()): RecyclerView.Adapter<MedicoAdapter.PetAdapterViewHolder>() {
    lateinit var setOnClickListenerMedicoEdit:(Medico) -> Unit
    lateinit var setOnClickListenerMedicoDelete:(Medico) -> Unit
    private var listener: OnMovieClickListener? = null
    lateinit var setOnclickListenerMedico:(Medico) -> Unit

    fun setOnMovieClickListener(listener: OnMovieClickListener) {
        this.listener = listener
    }

    //crear el viewHolder
    inner class PetAdapterViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        private var binding: ItemMedicoBinding = ItemMedicoBinding.bind(itemView)
        fun bind(medico: Medico){
            binding.txtCedula.text = medico.nombre
            binding.txtNombre.text = medico.id
            binding.txtEspecialidad.text = medico.especialidad
            binding.txtCorreoM.text = medico.telefono
            binding.root.setOnClickListener {
                setOnclickListenerMedico(medico)
            }
            binding.btnEditar.setOnClickListener{
                setOnClickListenerMedicoEdit(medico)
            }
            binding.btnEliminar.setOnClickListener{
                setOnClickListenerMedicoDelete(medico)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medico,parent,false)
        return PetAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetAdapterViewHolder, position: Int) {
        val medico = medicos[position]
        holder.bind(medico)
    }

    override fun getItemCount(): Int {
        return medicos.size
    }

    fun updateListPets(medicos:List<Medico>){
        this.medicos = medicos
        notifyDataSetChanged()
    }
    interface OnMovieClickListener {
        fun onMovieClick(medico: Medico)
    }
}