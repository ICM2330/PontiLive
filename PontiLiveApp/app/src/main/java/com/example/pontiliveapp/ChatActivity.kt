package com.example.pontiliveapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pontiliveapp.databinding.ActivityChatBinding
import com.example.pontiliveapp.databinding.ActivityMainBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var chatContainer: LinearLayout
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageEditText = findViewById(R.id.messageEditText)
        chatContainer = findViewById(R.id.chatContainer)

        setListeners();

    }

    fun setListeners(){
        binding.botonBack.setOnClickListener{
            startActivity(Intent(baseContext, ChatsMenuActivity::class.java))
        }
    }

    fun sendMessage(view: View) {
        val message = messageEditText.text.toString().trim()
        if (message.isNotEmpty()) {
            addMessageToChat(message)
            messageEditText.text.clear()
        }
    }

    private fun addMessageToChat(message: String) {
        val messageTextView = TextView(this)
        messageTextView.text = message

        // Set text alignment to the right
        messageTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

        // Set layout parameters to align to the right and stack from bottom
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.END

        messageTextView.layoutParams = layoutParams

        chatContainer.addView(messageTextView)

        // Scroll the ScrollView to the bottom
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
}
