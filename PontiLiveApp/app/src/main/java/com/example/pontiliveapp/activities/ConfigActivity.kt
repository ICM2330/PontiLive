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
import com.bumptech.glide.Glide
import com.example.pontiliveapp.databinding.ActivityConfigBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.parse.ParseUser
import java.io.File
import java.io.FileOutputStream

class ConfigActivity : AppCompatActivity() {

    private lateinit var uriFireBase : Uri


    private lateinit var uriUpload : Uri
    private lateinit var binding: ActivityConfigBinding
    val getContentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            loadImage(it!!)
            uriFireBase=it!!
            uploadFirebaseImage(uriFireBase)
        }
    )

    val getContentCamera = registerForActivityResult(ActivityResultContracts.TakePicture(),
        ActivityResultCallback {
            if(it){
                loadImage(cameraUri)

                uriFireBase=cameraUri
                uploadFirebaseImage(uriFireBase)
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


        binding.Aceptar.setOnClickListener{
            val nombreExtraido = binding.nombreConfig.text.toString()
            val descripcionExtraida = binding.descripcionConfig.text.toString()

            if (!nombreExtraido.toString().isEmpty()){
                currentUser.put("nombre",nombreExtraido)
            }

            if (!descripcionExtraida.toString().isEmpty()){
                currentUser.put("descripcion",descripcionExtraida)
            }



            currentUser.saveInBackground { e ->
                if (e == null) {
                    Log.e("Actualizacion", "Exitosa")
                } else {

                    val errorMessage = e.message
                    Log.e("Actualizacion", "Error: ",e)
                }
            }
            startActivity(Intent(baseContext, ProfileActivity::class.java))

        }

        binding.mapButton.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))


        }
        downloadUserImage(currentUser.objectId)

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
    fun downloadUserImage(objectID: String) {
        val storage = FirebaseStorage.getInstance()
        val imageRef = storage.reference.child("images/$objectID.png")

        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Asigna la URI a la variable lateinit
                uriUpload = uri
                // Use Glide to load the image
                Glide.with(this)
                    .load(uriUpload)
                    .into(binding.profileImageView)
            }
            .addOnFailureListener { exception ->
                println("Error al descargar la imagen: ${exception.message}")
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

    fun uploadFirebaseImage(uriUpload: Uri) {
        // Obtén una referencia al lugar donde las fotos serán guardadas
        val currentUser = ParseUser.getCurrentUser()
        val objectId = currentUser?.objectId
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("images/${objectId}.png")

        // Inicia la carga del archivo
        storageRef.putFile(uriUpload)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                // La carga fue exitosa, aquí puedes obtener, por ejemplo, la URL de la imagen
                val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
                downloadUrl?.addOnSuccessListener { uri ->
                    println("Imagen cargada con éxito. URL: $uri")
                }
            }
            .addOnFailureListener { exception: Exception ->
                // La carga falló, maneja el error
                println("Error al cargar la imagen: ${exception.message}")
            }
    }
}