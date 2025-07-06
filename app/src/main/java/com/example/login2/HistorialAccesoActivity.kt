package com.example.login2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class HistorialAccesoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistorialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_acceso)

        recyclerView = findViewById(R.id.recyclerViewHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val idUsuario = intent.getIntExtra("id_usuario", -1)

        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = "http://10.0.2.2:7011/api/acceso/historial?id_usuario=$idUsuario"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val accesos = mutableListOf<Acceso>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val nombre = obj.getString("nombre")
                    val fechaEntrada = obj.getString("fechaEntrada")
                    val fechaSalida = if (!obj.isNull("fechaSalida")) obj.getString("fechaSalida") else "En curso"
                    val autorizado = obj.getBoolean("autorizado")
                    accesos.add(Acceso(nombre, fechaEntrada, fechaSalida, autorizado))
                }
                adapter = HistorialAdapter(accesos)
                recyclerView.adapter = adapter
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error al obtener historial: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}
