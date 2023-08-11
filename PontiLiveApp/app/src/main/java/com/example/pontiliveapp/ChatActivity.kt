package com.example.pontiliveapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pontiliveapp.databinding.ActivityChatBinding
import com.example.pontiliveapp.databinding.ActivityConfigBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileButton.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))
        }

        binding.mapButton.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }
    }
}