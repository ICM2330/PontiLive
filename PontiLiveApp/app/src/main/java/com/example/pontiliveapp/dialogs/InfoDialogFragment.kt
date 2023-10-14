package com.example.pontiliveapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.pontiliveapp.databinding.FragmentInfoDialogBinding

class InfoDialogFragment(private val onSubmitClickListener: (Any?) -> Unit
): DialogFragment() {

    private lateinit var binding : FragmentInfoDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentInfoDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)


        binding.comenzarRuta.setOnClickListener {
            //onSubmitClickListener.invoke(binding.etAmount.text.toString().toFloat())
            dismiss()
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}