package com.example.pontiliveapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pontiliveapp.databinding.ActivityConfigBinding
import com.example.pontiliveapp.databinding.ActivityMapBinding

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatButton.setOnClickListener{
            startActivity(Intent(baseContext, ChatActivity::class.java))
        }

        binding.profileButton.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))
        }

        binding.backButton.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))
        }

        binding.mapButton.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }
    }
}