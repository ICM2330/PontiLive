package com.example.pontiliveapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pontiliveapp.databinding.ActivityRegistroBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonRegistrarseR.setOnClickListener{
            if (validateForm()){
                guardarUsuario()



                val intent = Intent (this, MainActivity::class.java)
                startActivity(intent)
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



    }

    fun saveData() {

            Log.i(TAG, "Intento de escritura en Parse")
            var firstObject = ParseObject(USER_CN)

            usuario = binding.inputUsuarioR.text.toString()
            nombre = binding.inputNombreR.text.toString()
            contrasena = binding.inputContraseAR.text.toString()

            firstObject.put("nombre",nombre)
            firstObject.put("usuario",usuario)
            firstObject.put("contrasena",contrasena)



            firstObject.saveInBackground { e ->
                if (e != null) {
                    Log.e(TAG, "error:",e)
                } else {
                    Log.d(TAG, "Objeto guardado.")
                    firstObject.unpinInBackground()
                }
            }

    }

    private fun guardarUsuario() {
        var userRegistro = ParseUser()
        userRegistro.username = binding.inputUsuarioR.text.toString()
        userRegistro.setPassword (binding.inputContraseAR.text.toString())

        nombre = binding.inputNombreR.text.toString()
        userRegistro.put("nombre",nombre)

        userRegistro.signUpInBackground{e:ParseException? ->
            if(e==null){

            }
            else{
                val errorMessage = e.message
                Log.e("Registro", "Error durante el registro: $errorMessage",e)
                // También puedes imprimir el código de error para tener más información
                Log.e("Registro", "Código de error: ${e.code}")
            }

        }
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

        return todasLasCasillasTienenTexto && contrasenasCoinciden
    }
}