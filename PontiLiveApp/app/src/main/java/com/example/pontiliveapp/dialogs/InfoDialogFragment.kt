package com.example.pontiliveapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.pontiliveapp.RouteActivity
import com.example.pontiliveapp.activities.MapActivity
import com.example.pontiliveapp.databinding.FragmentInfoDialogBinding
import com.example.pontiliveapp.model.getLugarName

//Clase para desplegar información sobre los lugares
//Para usarla se debe pasar por argumentos de la clase un bundle con el nombre del lugar a desplegar
//Por ejemplo:

/* *Dentro del Listener del elemento que despliege el díalogo (Botón, marcador...)*
* val bundle = Bundle()
* val fragB = InfoDialogFragment()
* bundle.putString("nombre", "Nombre del edificio")
* fragB.arguments = bundle
* fragB.show(supportFragmentManager, "dialog")
*/
class InfoDialogFragment() : DialogFragment() {

    private lateinit var binding : FragmentInfoDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentInfoDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val lugarB = arguments?.getString("nombre")?.let { getLugarName(it) }

        binding.lugar.text = lugarB!!.nombre
        binding.espacio.text = lugarB!!.espacio
        binding.poblacion.text = lugarB!!.poblacionActual.toString()
        binding.distancia.text = lugarB!!.distanciaActual.toString()
        binding.imagen.setImageResource(lugarB.IdImagen)



        binding.comenzarRuta.setOnClickListener{
            val i = Intent(context, RouteActivity::class.java)
            i.putExtra("nombre",lugarB!!.nombre);
            startActivity(Intent(i))
            dismiss()
        }


        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}