package com.example.login2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File

class AjustesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        val idUsuario = intent.getIntExtra("id_usuario", -1)
        val nombreUsuario = intent.getStringExtra("usuario") ?: "Usuario"
        val imagePath = intent.getStringExtra("image_path")

        val ivFotoPerfil = findViewById<ImageView>(R.id.ivFotoPerfil)
        val tvNombre = findViewById<TextView>(R.id.tvNombreUsuario)
        val ivQR = findViewById<ImageView>(R.id.ivCodigoQR)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        tvNombre.text = nombreUsuario

        if (!imagePath.isNullOrEmpty()) {
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                ivFotoPerfil.setImageBitmap(bitmap)
            } else {
                ivFotoPerfil.setImageResource(R.drawable.ic_person)
            }
        } else {
            ivFotoPerfil.setImageResource(R.drawable.ic_person)
        }

        try {
            val qrContent = "RES/$idUsuario"
            val barcodeEncoder = BarcodeEncoder()
            val bitmapQR = barcodeEncoder.encodeBitmap(
                qrContent,
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            ivQR.setImageBitmap(bitmapQR)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al generar el código QR", Toast.LENGTH_SHORT).show()
        }
    }
}

