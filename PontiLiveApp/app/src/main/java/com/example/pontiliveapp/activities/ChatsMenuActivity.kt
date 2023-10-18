package com.example.pontiliveapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pontiliveapp.databinding.ActivityChatsMenuBinding
import com.example.pontiliveapp.dialogs.InfoDialogFragment

class ChatsMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatsMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatsMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners();
    }

    fun setListeners(){
        binding.Back.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }

        binding.profilee.setOnClickListener{
            startActivity(Intent(baseContext, ProfileActivity::class.java))
        }

        binding.mapaa.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }

        binding.chat1.setOnClickListener{
            startActivity(Intent(baseContext, ChatActivity::class.java))
        }
    }
}

