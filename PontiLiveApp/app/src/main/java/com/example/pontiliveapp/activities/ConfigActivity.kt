package com.example.pontiliveapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.example.pontiliveapp.databinding.ActivityConfigBinding
import com.parse.ParseUser
import java.io.File
import java.io.FileOutputStream

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding
    val getContentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            loadImage(it!!)
            saveImageToInternalStorage(it!!)
        }
    )

    val getContentCamera = registerForActivityResult(ActivityResultContracts.TakePicture(),
        ActivityResultCallback {
            if(it){
                loadImage(cameraUri)
                saveImageToInternalStorage(cameraUri)
            }
        })
    lateinit var currentUser:ParseUser
    lateinit var cameraUri : Uri

    lateinit var urlImagen : String
    lateinit var nombre : String
    lateinit var descripcion : String


    override fun onCreate(savedInstanceState: Bundle?) {




        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()

        currentUser = ParseUser.getCurrentUser()



        binding.mapButton.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
            val nombreExtraido = binding.nombreConfig.text.toString()
            val descripcionExtraida = binding.descripcionConfig.text.toString()

            currentUser.put("nombre",nombreExtraido)
            currentUser.put("descripcion",descripcionExtraida)

            currentUser.saveInBackground { e ->
                if (e == null) {
                    Log.e("Actualizacion", "Exitosa")
                } else {

                    val errorMessage = e.message
                    Log.e("Actualizacion", "Error: ",e)
                }
            }

        }

        binding.nombreConfig.hint = currentUser.getString("nombre")
        binding.descripcionConfig.hint = currentUser.getString("descripcion")
        urlImagen = currentUser.getString("urlImagen").toString()



    }

    private fun setupButtons(){
        binding.profileImageView.setOnClickListener {
            mostrarDialogo()
        }

        binding.chatButton.setOnClickListener{
            startActivity(Intent(baseContext, ChatActivity::class.java))
        }

        binding.profileButton.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))
        }

        binding.backButton.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))

        }

        binding.logOutButton.setOnClickListener(){
            logOutApp()
        }

    }

    private fun logOutApp(){
        ParseUser.logOut()

        // Borrar el token de las preferencias compartidas
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("sessionToken") // Elimina el token
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun mostrarDialogo() {
        val builder = AlertDialog.Builder(this)

        // Establece el título y el mensaje del diálogo
        builder.setTitle("Subir imagen")
        builder.setMessage("¿Desea subir imagen con cámara o galería?")

        // Agrega un botón "Aceptar" con su acción
        builder.setPositiveButton("Cámara") { dialog, which ->

        val file = File(getFilesDir(), "picFromCamera");
        cameraUri = FileProvider.getUriForFile(baseContext,baseContext.packageName + ".fileprovider", file)
        getContentCamera.launch(cameraUri)

        }

        // Agrega un botón "Cancelar" con su acción
        builder.setNegativeButton("Galería") { dialog, which ->
            getContentGallery.launch("image/*")
        }

        // Crea el diálogo
        val dialog: AlertDialog = builder.create()

        // Muestra el diálogo
        dialog.show()
    }

    fun loadImage(uri : Uri){
        val imageStream = getContentResolver().openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        binding.profileImageView.setImageBitmap(bitmap)
    }
    private fun saveImageToInternalStorage(uri: Uri) {
        val imageStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(imageStream)

        // Directorio de almacenamiento interno
        val directory = File(filesDir, "images")
        directory.mkdirs()

        // Nombre del archivo para la imagen
        val currentUser = ParseUser.getCurrentUser()
        val fileName = currentUser.getString("username")+".jpg"
        urlImagen= fileName

        // Ruta completa del archivo
        val file = File(directory, fileName)

        // Guarda la imagen en el almacenamiento interno
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
    }
}