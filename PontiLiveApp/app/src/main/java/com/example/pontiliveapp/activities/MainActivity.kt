package com.example.pontiliveapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pontiliveapp.databinding.ActivityMainBinding
import com.parse.ParseAnonymousUtils
import com.parse.ParseException
import com.parse.ParseUser


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val USER_CN = "Usuario"

    var usuario = ""
    var contraseña = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ParseAnonymousUtils.logIn { user, e ->
            if (e == null) {
                // El inicio de sesión anónimo fue exitoso
                val userId = user.objectId
                // Realiza acciones con el usuario anónimo, si es necesario
            } else {
                // El inicio de sesión anónimo falló
                Log.e("Parse", "Error al iniciar sesión anónimamente: " + e.message)
            }
        }

        binding.botonIniciarSesion.setOnClickListener{
            usuario = binding.inputUsuario.text.toString()
            contraseña = binding.inputContrasena.text.toString()

            ParseUser.logInInBackground(usuario, contraseña) { user: ParseUser?, e: ParseException? ->
                if (user != null) {
                    val intent = Intent (this, MapActivity::class.java)
                    startActivity(intent)
                } else {
                    val toast = Toast.makeText(this, "El usuario no existe ", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }

        binding.botonRegistrarse.setOnClickListener{
            val intent = Intent (this, RegistroActivity::class.java)
            startActivity(intent)
        }

    }








}