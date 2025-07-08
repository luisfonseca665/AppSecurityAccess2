package com.example.login2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.widget.Toast
import java.io.File

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val idUsuario = intent.getIntExtra("id_usuario", -1)
        val nombreUsuario = intent.getStringExtra("usuario") ?: "Usuario"
        val imagePath = intent.getStringExtra("image_path")

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val ivProfilePhoto = findViewById<ImageView>(R.id.ivProfilePhoto)

        tvWelcome.text = "Bienvenido, $nombreUsuario"

        if (!imagePath.isNullOrEmpty()) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                ivProfilePhoto.setImageBitmap(bitmap)
            } else {
                ivProfilePhoto.setImageResource(R.drawable.ic_person)
            }
        } else {
            ivProfilePhoto.setImageResource(R.drawable.ic_person)
        }

        findViewById<ImageView>(R.id.btnSettings).setOnClickListener {
            val intent = Intent(this, AjustesActivity::class.java)
            intent.putExtra("id_usuario", idUsuario)
            intent.putExtra("usuario", nombreUsuario)
            intent.putExtra("image_path", imagePath)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnGenerarQR).setOnClickListener {
            startActivity(Intent(this, GenerarQRActivity::class.java))
        }

        findViewById<CardView>(R.id.btnMisInvitados).setOnClickListener {
            val intent = Intent(this, MisInvitadosActivity::class.java)
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnHistorialAcceso).setOnClickListener {
            val intent = Intent(this, HistorialAccesoActivity::class.java)
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.btnSalir).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        }
    }
}






