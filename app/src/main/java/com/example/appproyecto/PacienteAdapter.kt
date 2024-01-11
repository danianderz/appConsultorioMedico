package com.example.appproyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appproyecto.databinding.ItemPacienteBinding
import com.example.appproyecto.model.Paciente

class PacienteAdapter(var pasientes: List<Paciente> =
                        emptyList()): RecyclerView.Adapter<PacienteAdapter.PetAdapterViewHolder>() {
    lateinit var setOnClickListenerMedicoEdit:(Paciente) -> Unit
    lateinit var setOnClickListenerMedicoDelete:(Paciente) -> Unit
    private var listener: OnMovieClickListener? = null
    lateinit var setOnclickListenerMedico:(Paciente) -> Unit

    fun setOnMovieClickListener(listener: OnMovieClickListener) {
        this.listener = listener
    }

    //crear el viewHolder
    inner class PetAdapterViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        private var binding: ItemPacienteBinding = ItemPacienteBinding.bind(itemView)
        fun bind(medico: Paciente){
            binding.txtCedula.text = medico.nombre
            binding.txtNombre.text = medico.id
            binding.txtCorreo.text = medico.correo
            binding.txtTelefono.text = medico.telefono
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_paciente,parent,false)
        return PetAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetAdapterViewHolder, position: Int) {
        val medico = pasientes[position]
        holder.bind(medico)
    }

    override fun getItemCount(): Int {
        return pasientes.size
    }

    fun updateListPets(pasientes:List<Paciente>){
        this.pasientes = pasientes
        notifyDataSetChanged()
    }
    interface OnMovieClickListener {
        fun onMovieClick(medico: Paciente)
    }
}