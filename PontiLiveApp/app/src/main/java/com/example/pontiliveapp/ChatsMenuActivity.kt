package com.example.pontiliveapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pontiliveapp.databinding.ActivityChatsMenuBinding

class ChatsMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatsMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatsMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profilee.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))
        }

        binding.mapaa.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }

        binding.chat1.setOnClickListener{
            startActivity(Intent(baseContext, ChatsMenuActivity::class.java))
        }



        setListeners();

    }

    fun setListeners(){
        binding.Back.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }
    }

}