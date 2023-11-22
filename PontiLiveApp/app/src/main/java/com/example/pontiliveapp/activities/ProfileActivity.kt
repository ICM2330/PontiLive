package com.example.pontiliveapp.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.pontiliveapp.databinding.ActivityProfileBinding
import com.example.pontiliveapp.dialogs.InfoDialogFragment
import com.example.pontiliveapp.dialogs.ListDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.parse.ParseUser
import java.io.File


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    val bundle = Bundle()
    val fragB = InfoDialogFragment()
    private lateinit var uriUpload : Uri

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentUser = ParseUser.getCurrentUser()

        val nomdb = currentUser.getString("nombre")
        val descdb = currentUser.getString("descripcion")
        val personalizable1db = currentUser.getString("personalizable1")
        val personalizable2db =currentUser.getString("personalizable2")
        binding.nombreUsuario.text = nomdb
        binding.descripcionUsuario.text = descdb
        binding.personalizable1.text = personalizable1db
        binding.personalizable2.text = personalizable2db

        Log.d("Parse", "Nombre: $nomdb")
        Log.d("Parse", "Descripción: $descdb")
        Log.d("Parse", "Personalizable1: $personalizable1db")
        Log.d("Parse", "Personalizable2: $personalizable2db")

        val urlImg = currentUser.getString("urlImagen")
        if (urlImg != null){
            val directorio = this.filesDir // Directorio de almacenamiento interno
            val rutaImagenCompleta = File(directorio, urlImg).path
            val bitmap = BitmapFactory.decodeFile(rutaImagenCompleta)
            binding.imagenUsuario.setImageBitmap(bitmap)
        }

        val parseUser = ParseUser.getCurrentUser()

        downloadUserImage(parseUser.objectId)

        setListeners()

    }

    fun setListeners(){
        binding.chatButton.setOnClickListener{
            startActivity(Intent(baseContext, ChatsMenuActivity::class.java))
        }

        binding.configButton.setOnClickListener{
            startActivity(Intent(baseContext, ConfigActivity::class.java))
        }

        binding.mapButton.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }

        binding.ing.setOnClickListener{
            bundle.putString("nombre", "Edificio de Ingeniería")
            fragB.arguments = bundle
            fragB.show(supportFragmentManager, "dialog")
        }

        binding.arca.setOnClickListener{
            bundle.putString("nombre", "Arca")
            fragB.arguments = bundle
            fragB.show(supportFragmentManager, "dialog")
        }

        binding.arqui.setOnClickListener{
            bundle.putString("nombre", "Arqui-diseño")
            fragB.arguments = bundle
            fragB.show(supportFragmentManager, "dialog")
        }

        binding.artes.setOnClickListener{
            bundle.putString("nombre", "Artes")
            fragB.arguments = bundle
            fragB.show(supportFragmentManager, "dialog")
        }

        binding.morePlaces.setOnClickListener{
            ListDialogFragment().show(supportFragmentManager, "dialog")
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
                    .into(binding.imagenUsuario)
            }
            .addOnFailureListener { exception ->
                println("Error al descargar la imagen: ${exception.message}")
            }
    }



}