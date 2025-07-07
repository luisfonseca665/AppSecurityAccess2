package com.example.login2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class MisInvitadosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InvitadosAdapter
    private val listaInvitados = mutableListOf<Invitado>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_invitados)

        recyclerView = findViewById(R.id.recyclerViewInvitados)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = InvitadosAdapter(listaInvitados)
        recyclerView.adapter = adapter

        val idUsuario = intent.getIntExtra("id_usuario", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        obtenerInvitadosPorUsuario(idUsuario)
    }

    private fun obtenerInvitadosPorUsuario(idUsuario: Int) {
        val url = "http://192.168.1.25:7011/api/Invitado/mis-invitados/$idUsuario"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                parsearInvitados(response)
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun parsearInvitados(jsonArray: JSONArray) {
        listaInvitados.clear()
        for (i in 0 until jsonArray.length()) {
            val item: JSONObject = jsonArray.getJSONObject(i)

            val nombre = item.optString("nombre", "Sin nombre")
            val fechaISO = item.optString("fecha", "")
            val estado = item.optString("estado", "Desconocido")
            val fechaFormateada = try {
                val formatoEntrada = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                val fechaDate = formatoEntrada.parse(fechaISO)
                val formatoSalida = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                formatoSalida.format(fechaDate)
            } catch (e: Exception) {
                "Fecha inválida"
            }
            listaInvitados.add(Invitado(nombre, fechaFormateada, estado))
        }
        adapter.notifyDataSetChanged()
    }
}