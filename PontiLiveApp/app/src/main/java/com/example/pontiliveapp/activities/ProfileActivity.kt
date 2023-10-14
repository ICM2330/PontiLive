package com.example.pontiliveapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pontiliveapp.databinding.ActivityProfileBinding
import com.example.pontiliveapp.dialogs.InfoDialogFragment
import com.example.pontiliveapp.dialogs.ListDialogFragment

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()

    }

    fun setListeners(){
        binding.chatButton.setOnClickListener{
            startActivity(Intent(baseContext, ChatsMenuActivity::class.java))
        }

        binding.configButton.setOnClickListener{
            startActivity(Intent(baseContext, ConfigActivity::class.java))
        }

        binding.mapButton.setOnClickListener{
            startActivity(Intent(baseContext, MapActivity::class.java))
        }

        binding.ing.setOnClickListener{
            InfoDialogFragment { quantity ->
                Toast.makeText(this, "Éxito", Toast.LENGTH_SHORT).show()
            }.show(supportFragmentManager, "dialog")
        }

        binding.morePlaces.setOnClickListener{
            ListDialogFragment { quantity ->
            Toast.makeText(this, "Éxito", Toast.LENGTH_SHORT).show()
        }.show(supportFragmentManager, "dialog")

        }
    }
}