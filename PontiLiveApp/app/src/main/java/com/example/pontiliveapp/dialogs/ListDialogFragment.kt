package com.example.pontiliveapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.pontiliveapp.activities.MapActivity
import com.example.pontiliveapp.databinding.FragmentListDialogBinding

class ListDialogFragment(): DialogFragment() {

    private lateinit var binding : FragmentListDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentListDialogBinding.inflate(LayoutInflater.from(context))
        setListeners()

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    fun setListeners(){
        binding.imagen1.setOnClickListener {
            val i = Intent(context, MapActivity::class.java)
            i.putExtra("nombre","Donde Cris");
            startActivity(Intent(i))
        }
        binding.imagen2.setOnClickListener {
            val i = Intent(context, MapActivity::class.java)
            i.putExtra("nombre","Biblioteca General");
            startActivity(Intent(i))
        }
        binding.imagen3.setOnClickListener {
            val i = Intent(context, MapActivity::class.java)
            i.putExtra("nombre","Básicas");
            startActivity(Intent(i))
        }
        binding.imagen4.setOnClickListener {
            val i = Intent(context, MapActivity::class.java)
            i.putExtra("nombre","Parque Nacional");
            startActivity(Intent(i))
        }
        binding.imagen5.setOnClickListener {
            val i = Intent(context, MapActivity::class.java)
            i.putExtra("nombre","Canchas Sintéticas");
            startActivity(Intent(i))
        }
        binding.imagen6.setOnClickListener {
            val i = Intent(context, MapActivity::class.java)
            i.putExtra("nombre","Edificio Giraldo");
            startActivity(Intent(i))
        }
    }
}