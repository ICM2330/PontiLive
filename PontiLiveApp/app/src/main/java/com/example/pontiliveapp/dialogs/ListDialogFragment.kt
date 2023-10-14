package com.example.pontiliveapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pontiliveapp.R
import com.example.pontiliveapp.databinding.FragmentInfoDialogBinding
import com.example.pontiliveapp.databinding.FragmentListDialogBinding

class ListDialogFragment(private val onSubmitClickListener: (Any?) -> Unit
): DialogFragment() {

    private lateinit var binding : FragmentListDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentListDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}