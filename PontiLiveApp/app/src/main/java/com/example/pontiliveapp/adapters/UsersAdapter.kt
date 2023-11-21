package com.example.pontiliveapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.pontiliveapp.R
import com.example.pontiliveapp.activities.ChatActivity


import com.example.pontiliveapp.model.Usuario

class UsersAdapter(context: Context, userList: List<Usuario>) :
    ArrayAdapter<Usuario>(context, 0, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView

        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.row_chat, parent, false)
        }

        val usuario = getItem(position)

        val nombreTextView = listItemView?.findViewById<TextView>(R.id.textNameRowChat)
        val fotoImageView = listItemView?.findViewById<ImageView>(R.id.imageProfileRowChat)
        //val lastMessage = listItemView?.findViewById<TextView>(R.id.textMessageRowChat)

        nombreTextView?.text = usuario?.usuario
        val imageUrl = usuario?.urlImagen

        // Carga la imagen usando la URI de la foto (puedes usar una biblioteca como Glide o Picasso)
        // Ejemplo ficticio, debes implementar la carga de la imagen desde la URI
        if (fotoImageView != null) {
            Glide.with(context).load(imageUrl).into(fotoImageView)
        }

        // Configura el OnClickListener para el botón
        listItemView?.setOnClickListener {
            // Inicia DetailActivity y pasa el objectID como un extra
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("objectID", usuario?.objectID)
            intent.putExtra("username",usuario?.usuario)
            intent.putExtra("urlImagen",usuario?.urlImagen)
            context.startActivity(intent)
        }

        return listItemView!!
    }
}