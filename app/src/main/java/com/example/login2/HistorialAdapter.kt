package com.example.login2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistorialAdapter(private val listaAccesos: List<Acceso>) :
    RecyclerView.Adapter<HistorialAdapter.AccesoViewHolder>() {

    class AccesoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreAcceso)
        val tvFechaHora: TextView = itemView.findViewById(R.id.tvFechaHoraAcceso)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatusAcceso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccesoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return AccesoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccesoViewHolder, position: Int) {
        val acceso = listaAccesos[position]
        holder.tvNombre.text = "Usuario: ${acceso.nombre}"
        holder.tvFechaHora.text = "Entrada: ${acceso.fechaEntrada}\nSalida: ${acceso.fechaSalida ?: "En curso"}"

        holder.tvStatus.text = if (acceso.autorizado) {
            "Actualmente dentro"
        } else {
            "Salida registrada"
        }
    }

    override fun getItemCount(): Int = listaAccesos.size
}
