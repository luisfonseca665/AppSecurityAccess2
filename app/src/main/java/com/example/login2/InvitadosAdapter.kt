package com.example.login2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InvitadosAdapter(private val invitados: List<Invitado>) :
    RecyclerView.Adapter<InvitadosAdapter.InvitadoViewHolder>() {

    class InvitadoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreInvitado)
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHoraInvitado)
        val tvEstado: TextView = view.findViewById(R.id.tvEstadoInvitado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invitado, parent, false)
        return InvitadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvitadoViewHolder, position: Int) {
        val invitado = invitados[position]
        holder.tvNombre.text = invitado.nombre
       holder.tvFechaHora.text = invitado.fechaHora
        holder.tvEstado.text = "Estado: ${invitado.estado}"
    }

    override fun getItemCount() = invitados.size
}

