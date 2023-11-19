package com.example.pontiliveapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pontiliveapp.databinding.ActivityRegistroBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.parse.ParseACL
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.SaveCallback

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    var usuario =""
    var nombre = ""
    var contrasena=""
    var confirmarContrasena=""

    val TAG = "GREETING_APP"
    val USER_CN = "Usuario"

    private var isImageSelected = false
    private lateinit var uriUpload : Uri

    val getContentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            loadImage(it!!)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonRegistrarseR.setOnClickListener{
            if (validateForm()){
                guardarUsuario()

            }
            else{
                val toast = Toast.makeText(this, "Información Inválida", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        binding.backButtonR.setOnClickListener{
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.imageView12.setOnClickListener{
            getContentGallery.launch("image/*")
        }
    }


    private fun guardarUsuario() {
        var userRegistro = ParseUser()
        userRegistro.username = binding.inputUsuarioR.text.toString()
        userRegistro.setPassword (binding.inputContraseAR.text.toString())

        nombre = binding.inputNombreR.text.toString()
        userRegistro.put("nombre",nombre)

        val acl = ParseACL()
        acl.publicReadAccess = true
        userRegistro.acl=acl

        userRegistro.signUpInBackground{e:ParseException? ->
            if(e==null){
                // Registro exitoso, guarda el token de sesión en SharedPreferences
                val sessionToken = userRegistro.sessionToken
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("sessionToken", sessionToken)
                editor.apply()
                Log.e("PARSE", "Registro exitoso: $sessionToken")
                val intent = Intent (this, MapActivity::class.java)
                startActivity(intent)
            }
            else{
                val errorMessage = e.message
                Log.e("Registro", "Error durante el registro: $errorMessage",e)
                // También puedes imprimir el código de error para tener más información
                Log.e("Registro", "Código de error: ${e.code}")
            }

        }

        uploadFirebaseImage(uriUpload)
    }

    private fun validateForm(): Boolean {
        val usuario = binding.inputUsuarioR.text.toString()
        val contrasena = binding.inputContraseAR.text.toString()
        val confirmarContrasena = binding.inputContraseAConfirmaRr.text.toString()
        val nombre = binding.inputNombreR.text.toString()

        val todasLasCasillasTienenTexto = usuario.isNotEmpty() &&
                contrasena.isNotEmpty() &&
                confirmarContrasena.isNotEmpty() &&
                nombre.isNotEmpty()

        val contrasenasCoinciden = contrasena == confirmarContrasena

        return todasLasCasillasTienenTexto && contrasenasCoinciden && isImageSelected
    }

    fun loadImage(uri : Uri){
        val imageStream = getContentResolver().openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        binding.imageView12.setImageBitmap(bitmap)
        isImageSelected = true
        uriUpload=uri
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