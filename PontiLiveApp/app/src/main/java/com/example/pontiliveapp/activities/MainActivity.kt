package com.example.pontiliveapp.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pontiliveapp.databinding.ActivityMainBinding
import com.example.pontiliveapp.notifications.sendNotification
import com.parse.ParseAnonymousUtils
import com.parse.ParseException
import com.parse.ParseUser


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val permitCode = 123

    val USER_CN = "Usuario"

    var usuario = ""
    var contraseña = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        checkAndRequestNotificationPermissions()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Autenticación automática con parse-server
        loginParse()

        setupLoginButon()



        binding.botonRegistrarse.setOnClickListener{
            val intent = Intent (this, RegistroActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupLoginButon(){
        binding.botonIniciarSesion.setOnClickListener{
            usuario = binding.inputUsuario.text.toString()
            contraseña = binding.inputContrasena.text.toString()

            ParseUser.logInInBackground(usuario, contraseña) { user: ParseUser?, e: ParseException? ->
                if (user != null) {
                    val sessionToken = user.sessionToken
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("sessionToken", sessionToken)
                    editor.apply()
                    Log.e("PARSE", "Login exitoso: $sessionToken")
                    val intent = Intent (this, MapActivity::class.java)
                    startActivity(intent)
                } else {
                    val toast = Toast.makeText(this, "El usuario no existe ", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Verifica si el permiso ya está concedido
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si el permiso no está concedido, solicítalo al usuario
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    permitCode
                )
            } else {
                showToast("Permiso de notificación concedido")
            }
        } else {
            showToast("La versión de Android no requiere solicitar permisos en tiempo de ejecución.")
        }
    }

    // Función para mostrar un Toast con el mensaje especificado
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permitCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Permiso de notificación concedido")
            } else {
                showToast("Permiso de notificación denegado")
            }
        }
    }

    private fun loginParse(){
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val sessionToken = sharedPreferences.getString("sessionToken", null)

        if (sessionToken != null) {
            // Iniciar sesión automáticamente con el token de sesión
            ParseUser.becomeInBackground(sessionToken) { user: ParseUser?, e: ParseException? ->
                if (user != null) {
                    val toast = Toast.makeText(
                        this,
                        "Token de usuario recuperado: ${user.username}",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()

                    user.put("estado","F")
                    user.saveInBackground { e ->
                        if (e == null) {
                            Log.i("ACT-ESTADO","El estado se setea en false")
                        } else {
                            // Manejar errores, por ejemplo, mostrar un mensaje al usuario
                        }
                    }

                    val intent = Intent (this, MapActivity::class.java)
                    startActivity(intent)
                } else {
                    val toast = Toast.makeText(
                        this,
                        "Error al iniciar sesión automáticamente",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    eliminarTokenDeSesion()
                }
            }
        }else{
            ParseAnonymousUtils.logIn { user, e ->
                if (e == null) {
                    // El inicio de sesión anónimo fue exitoso
                    val userId = user.objectId
                    // Realiza acciones con el usuario anónimo, si es necesario
                } else {
                    // El inicio de sesión anónimo falló
                    Log.e("Parse", "Error al iniciar sesión anónimamente: " + e.message)
                    Log.e("PARSE",e.stackTraceToString())
                }
                Log.d("PARSE","entra a anonimo")
            }

        }
    }

    private fun eliminarTokenDeSesion() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("sessionToken")
        editor.apply()

        ParseAnonymousUtils.logIn { user, e ->
            if (e == null) {
                // El inicio de sesión anónimo fue exitoso
                val userId = user.objectId
                // Realiza acciones con el usuario anónimo, si es necesario
            } else {
                // El inicio de sesión anónimo falló
                Log.e("Parse", "Error al iniciar sesión anónimamente: " + e.message)
                Log.e("PARSE",e.stackTraceToString())
            }
            Log.d("PARSE","entra a anonimo")
        }
    }

}