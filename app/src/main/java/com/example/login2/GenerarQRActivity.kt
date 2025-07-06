package com.example.login2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class GenerarQRActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellidoPaterno: EditText
    private lateinit var etApellidoMaterno: EditText
    private lateinit var etFechaVigencia: EditText
    private lateinit var spinnerTipoInvitacion: Spinner
    private lateinit var btnGenerarCodigo: Button
    private lateinit var btnVerCompartirPDF: Button
    private lateinit var ivCodigoQR: ImageView

    private var idUsuarioSesion: Int = -1
    private var rutaPDFGenerado: String? = null

    private val listaTipoInvitacion = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generar_qr)

        etNombre = findViewById(R.id.etNombre)
        etApellidoPaterno = findViewById(R.id.etApellidoPaterno)
        etApellidoMaterno = findViewById(R.id.etApellidoMaterno)
        etFechaVigencia = findViewById(R.id.etFechaVigencia)
        spinnerTipoInvitacion = findViewById(R.id.spinnertipoinvitacion)
        btnGenerarCodigo = findViewById(R.id.btnGenerarCodigo)
        btnVerCompartirPDF = findViewById(R.id.btnVerCompartirPDF)
        ivCodigoQR = findViewById(R.id.ivCodigoQR)

        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        idUsuarioSesion = prefs.getInt("ID_USUARIO", -1)
        if (idUsuarioSesion == -1) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarTiposDeInvitacionDesdeApi()

        btnGenerarCodigo.setOnClickListener {
            insertarInvitadoYGenerarQR()
        }

        btnVerCompartirPDF.setOnClickListener {
            abrirYCompartirPDF()
        }
    }

    private fun cargarTiposDeInvitacionDesdeApi() {
        val url = "http://10.0.2.2:7011/api/invitado/tipos-invitacion"
        val queue = Volley.newRequestQueue(this)
        val request = JsonArrayRequest(url,
            { response ->
                listaTipoInvitacion.clear()
                for (i in 0 until response.length()) {
                    listaTipoInvitacion.add(response.getString(i))
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaTipoInvitacion)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTipoInvitacion.adapter = adapter
            },
            { error ->
                Toast.makeText(this, "Error cargando tipos de invitación: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)
    }

    private fun insertarInvitadoYGenerarQR() {
        val nombre = etNombre.text.toString().trim()
        val apellidoPaterno = etApellidoPaterno.text.toString().trim()
        val apellidoMaterno = etApellidoMaterno.text.toString().trim()
        var fechaVigencia = etFechaVigencia.text.toString().trim()
        val tipoInvitacionSeleccionado = spinnerTipoInvitacion.selectedItem?.toString() ?: ""

        if (nombre.isEmpty() || apellidoPaterno.isEmpty() || tipoInvitacionSeleccionado.isEmpty() || fechaVigencia.isEmpty()) {
            Toast.makeText(this, "Por favor llena los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(fechaVigencia)
            fechaVigencia = dateFormat.format(date!!)
        } catch (e: Exception) {
            Toast.makeText(this, "Formato de fecha incorrecto. Debe ser YYYY-MM-DD", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://10.0.2.2:7011/api/invitado/registrar"
        val jsonBody = JSONObject().apply {
            put("nombre", nombre)
            put("apellido_paterno", apellidoPaterno)
            put("apellido_materno", apellidoMaterno)
            put("fecha_vigencia", fechaVigencia)
            put("id_usuario", idUsuarioSesion)
            put("tipo_invitacion", tipoInvitacionSeleccionado)
        }

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            { response ->
                val invitadoId = response.optInt("invitadoId", -1)
                if (invitadoId != -1) {
                    val textoQR = "IN/${invitadoId}"
                    try {
                        val barcodeEncoder = BarcodeEncoder()
                        val bitmap = barcodeEncoder.encodeBitmap(textoQR, BarcodeFormat.QR_CODE, 400, 400)
                        ivCodigoQR.setImageBitmap(bitmap)
                        generarPDFConQR(bitmap, textoQR)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error generando QR: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error: Invitado no registrado correctamente.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val errorMsg = String(error.networkResponse?.data ?: ByteArray(0))
                Toast.makeText(this, "Error al insertar invitado: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("GenerarQR", "Detalles: $errorMsg")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Content-Type" to "application/json")
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

    private fun generarPDFConQR(bitmap: Bitmap, textoQR: String) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintText = android.graphics.Paint().apply {
            textSize = 20f
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }

        val paintTextSmall = android.graphics.Paint().apply {
            textSize = 14f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        // Título centrado
        canvas.drawText("Código QR de Invitación", (pageWidth / 2).toFloat(), 80f, paintText)

        // Escalar el bitmap a 300x300
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false)

        // Dibujar el QR centrado
        val left = (pageWidth - scaledBitmap.width) / 2
        val top = 120
        canvas.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), null)

        // Texto QR centrado abajo del código
        canvas.drawText(textoQR, (pageWidth / 2).toFloat(), (top + 320).toFloat(), paintTextSmall)

        pdfDocument.finishPage(page)

        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "codigo_qr_invitacion.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        rutaPDFGenerado = file.absolutePath
        Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_SHORT).show()
    }


    private fun abrirYCompartirPDF() {
        if (rutaPDFGenerado == null) {
            Toast.makeText(this, "Primero genera el QR para crear el PDF", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(rutaPDFGenerado!!)
        val uri: Uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Compartir PDF usando"))
    }
}



