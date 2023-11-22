package com.example.pontiliveapp.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pontiliveapp.databinding.ActivityChatsMenuBinding
import com.example.pontiliveapp.dialogs.InfoDialogFragment
import com.example.pontiliveapp.model.Usuario
import com.example.pontiliveapp.adapters.UsersAdapter
import com.google.firebase.storage.FirebaseStorage
import com.parse.ParseUser

class ChatsMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatsMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatsMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()

        updateUsers()

    }

    fun setButtons(){
        binding.menuMensajes.setOnClickListener{
            startActivity(Intent(baseContext, ChatsMenuActivity::class.java))
        }

        binding.menuMapa.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }

        binding.menuProfile.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))
        }

        binding.Back.setOnClickListener{
            onBackPressed()
        }
    }


    val fotos = mutableListOf<String>()
    val usuarios = mutableListOf<Usuario>()

    private fun updateUsers() {
        val currentUser = ParseUser.getCurrentUser()
        val query = ParseUser.getQuery()
        var cont = 0

        if (currentUser != null) {
            query.whereNotEqualTo("objectId", currentUser.objectId)
        }

        query.findInBackground { userObjects, e ->
            if (e == null) {
                val usernameList = mutableListOf<String>()
                val objectID = mutableListOf<String>()

                userObjects?.forEach { user ->
                    user?.let { username ->
                        usernameList.add(user.username)
                        objectID.add(user.objectId)
                    }
                }

                println("Lista de nombres de usuario : $usernameList")
                println("Lista de ObjectsID : $objectID")

                downloadUserImages(objectID) { uris ->
                    for (uri in uris) {
                        println("URI descargada: $uri")
                        fotos.add(uri.toString())
                        cont += 1
                    }
                    if(usernameList.size == fotos.size){
                        for (i in 0 until usernameList.size){
                            usuarios.add(Usuario(usernameList[i], fotos[i], objectID[i]))
                        }
                        println("Lista de usuario : $usuarios")


                        val adapter = UsersAdapter(this, usuarios)
                        println("Antes")

                        // Asigna el adaptador a tu ListView
                        binding.listView.adapter = adapter
                        println("Despues")
                    }
                }

            } else {
                println("Error al buscar usuarios: " + e.localizedMessage)
                // Manejar el error aquí

                // Asegurarse de que la función updateUsers termine aquí en caso de error
                println("Termina UpdateUsers")
            }
            println("Termina Query")
        }
        println("Termina UpdateUsers Final")
    }


    fun downloadUserImages(objectID: List<String>, completion: (List<Uri>) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        // Crear una lista vacía para almacenar las URIs
        val imageUris = mutableListOf<Uri>()

        // Variable de conteo para saber cuándo hemos terminado
        var downloadCount = 0

        // Itera sobre la lista de nombres de usuario
        for (username in objectID) {
            // Reemplaza cualquier caracter que no sea adecuado para un nombre de archivo.
            val sanitizedUsername = username

            // Obtiene la referencia de la imagen para el usuario actual
            val imageRef = storage.reference.child("images/$sanitizedUsername.png")

            // Obtiene la URL de descarga
            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    // Agrega la URI a la lista
                    imageUris.add(uri)

                    // Incrementa el conteo de descargas
                    downloadCount++

                    // Si hemos recibido todas las URIs, pasamos la lista a la función de completado
                    if (downloadCount == objectID.size) {
                        completion(imageUris)
                    }
                }
                .addOnFailureListener { exception ->
                    // Incrementa el conteo de descargas incluso en caso de fallo
                    downloadCount++

                    // Maneja el caso de error aquí, tal vez quieras registrar los errores o añadir un valor nulo a la lista
                    println("Error al descargar la imagen de $sanitizedUsername: ${exception.message}")

                    // Aún en caso de fallo, debemos verificar si hemos terminado
                    if (downloadCount == objectID.size) {
                        completion(imageUris)
                    }
                }
        }
    }



}

