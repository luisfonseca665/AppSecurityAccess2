package com.example.login2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Por favor, ingresa todos los campos")
                return@setOnClickListener
            }

            val jsonBody = JSONObject().apply {
                put("usuario", username)
                put("password", password)
            }

            val url = "http://10.0.2.2:7011/api/auth/login"

            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                { response ->
                    try {
                        val id = response.getInt("id")
                        val usuario = response.getString("usuario")
                        val imagenBase64 = response.optString("imagen", "")

                        var imagePath: String? = null
                        if (imagenBase64.isNotEmpty()) {
                            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                            val file = File(cacheDir, "profile.jpg")
                            FileOutputStream(file).use { it.write(imageBytes) }
                            imagePath = file.absolutePath
                        }

                        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        prefs.edit().putInt("ID_USUARIO", id).apply()

                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("id_usuario", id)
                            putExtra("usuario", usuario)
                            putExtra("image_path", imagePath)
                        }
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast("Error al procesar la respuesta del servidor")
                    }
                },
                { error ->
                    error.printStackTrace()
                    showToast("Error al iniciar sesión: ${error.message ?: "Error desconocido"}")
                }
            )

            Volley.newRequestQueue(this).add(request)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
