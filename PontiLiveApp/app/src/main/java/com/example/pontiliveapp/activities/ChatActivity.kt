package com.example.pontiliveapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pontiliveapp.R
import com.example.pontiliveapp.databinding.ActivityChatBinding
import com.parse.ParseACL
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling


class ChatActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var chatContainer: LinearLayout
    private lateinit var binding: ActivityChatBinding

    lateinit var parseLiveQueryClient: ParseLiveQueryClient

    lateinit var parseQuery:ParseQuery<ParseObject>

    lateinit var queryEmisorReceptor:ParseQuery<ParseObject>
    lateinit var queryReceptorEmisor:ParseQuery<ParseObject>
    lateinit var idChatContrario:String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idChatContrario = intent.getStringExtra("objectID").toString()
        val usernameReceptor = intent.getStringExtra("username").toString()
        val urlReceptor = intent.getStringExtra("urlImagen").toString()

        if (urlReceptor != null){
            Glide.with(this.baseContext).load(urlReceptor).into(binding.profileImageView)
        }
        binding.recipientNameTextView.text = usernameReceptor

        messageEditText = findViewById(R.id.messageEditText)
        chatContainer = findViewById(R.id.chatContainer)

        setListeners();

        loadExistingMessages()

        initParseLiveQuery()

        setupSubscription()


    }

    private fun initParseLiveQuery() {
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
    }

    fun setListeners(){
        binding.botonBack.setOnClickListener{
            startActivity(Intent(baseContext, ChatsMenuActivity::class.java))
        }
    }

    fun sendMessage(view: View) {
        val message = messageEditText.text.toString().trim()
        if (message.isNotEmpty()) {
            addMessageToChat(message, true)
            messageEditText.text.clear()
            subirMensajeParse(message)
        }
    }

    fun subirMensajeParse (message: String){
        val currentUser = ParseUser.getCurrentUser()
        val objectIdPropio= currentUser.objectId
        val mensajeParse = ParseObject("mensaje")
        mensajeParse.put("emisor",objectIdPropio)
        mensajeParse.put("receptor",idChatContrario)
        mensajeParse.put("contenidoMensaje",message)

        val acl = ParseACL()
        acl.publicReadAccess = true
        mensajeParse.acl=acl

        mensajeParse.saveInBackground{e ->
            if (e == null){
                Log.i("PARSE", "Mensaje guardado con éxito en PARSE")
            } else {
                Log.e("PARSE", "Error al guardar: "+e.localizedMessage)
            }

        }
    }

    private fun addMessageToChat(message: String?, mensajePropio: Boolean) {


        val messageTextView = TextView(this)
        messageTextView.text = message

        // Set text alignment to the right

        if (mensajePropio){
            messageTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            messageTextView.setBackgroundResource(R.drawable.message_background)
        }
        else{
            messageTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            messageTextView.setBackgroundResource(R.drawable.message_background2)
        }


        // Set layout parameters to align to the right and stack from bottom
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin=16
            bottomMargin=16
        }
        layoutParams.gravity = Gravity.END
        if (mensajePropio){
            layoutParams.gravity = Gravity.END
        }
        else{
            layoutParams.gravity = Gravity.START
        }

        messageTextView.layoutParams = layoutParams

        chatContainer.addView(messageTextView)

        // Scroll the ScrollView to the bottom
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun loadExistingMessages() {
        val currentUser = ParseUser.getCurrentUser()
        val objectIdPropio= currentUser.objectId
        println("mi id propio: $objectIdPropio, idReceptor: $idChatContrario")

        // Asegúrate de cambiar 'Mensaje' por el nombre de tu clase en Parse
        queryEmisorReceptor = ParseQuery.getQuery("mensaje")
        queryEmisorReceptor.whereEqualTo("emisor", objectIdPropio)
        queryEmisorReceptor.whereEqualTo("receptor", idChatContrario)

        queryReceptorEmisor = ParseQuery.getQuery("mensaje")
        queryReceptorEmisor.whereEqualTo("emisor", idChatContrario)
        queryReceptorEmisor.whereEqualTo("receptor", objectIdPropio)

        val combinedQuery = ParseQuery.or(listOf(queryEmisorReceptor, queryReceptorEmisor))
        combinedQuery.orderByAscending("updatedAt")

        combinedQuery.findInBackground { mensajes, e ->
            if (e == null) {
                mensajes.forEach { mensaje ->

                    val emisor = mensaje.getString("emisor")
                    val receptor = mensaje.getString("receptor")
                    val contenidoMensaje = mensaje.getString("contenidoMensaje")
                    println("mensaje: $contenidoMensaje,emisor: $emisor, receptor: $receptor")

                    if (emisor==objectIdPropio){
                        addMessageToChat(contenidoMensaje,true)
                    }
                    else{
                        addMessageToChat(contenidoMensaje,false)
                    }


                }
            } else {
                // Manejar el error
            }
        }
    }

    private fun setupSubscription() {
        val currentUser = ParseUser.getCurrentUser()
        val objectIdPropio= currentUser.objectId
        // Suponiendo que 'emisorId' y 'receptorId' son los valores que tienes guardados en tu actividad

        parseQuery = ParseQuery.getQuery("mensaje")
        parseQuery.whereEqualTo("emisor", idChatContrario)
        parseQuery.whereEqualTo("receptor", objectIdPropio)

        // Suscribirse solo a los eventos de creación de nuevos objetos
        val subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery)

        // Reaccionar a los eventos de creación
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE) { _, mensaje ->
            mensaje?.let {
                val emisor = mensaje.getString("emisor")
                val receptor = mensaje.getString("receptor")
                val contenidoMensaje = mensaje.getString("contenidoMensaje")
                println("mensaje recibido: $contenidoMensaje, de $emisor")

                if (emisor!=objectIdPropio){
                    runOnUiThread {
                        addMessageToChat(contenidoMensaje,false)
                    }
                }

            }
        }
    }







}
