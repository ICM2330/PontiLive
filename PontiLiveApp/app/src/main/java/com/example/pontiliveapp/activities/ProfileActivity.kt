package com.example.pontiliveapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pontiliveapp.databinding.ActivityProfileBinding
import com.example.pontiliveapp.dialogs.InfoDialogFragment
import com.example.pontiliveapp.dialogs.ListDialogFragment

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    val bundle = Bundle()
    val fragB = InfoDialogFragment()

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
            bundle.putString("nombre", "Edificio de Ingeniería")
            fragB.arguments = bundle
            fragB.show(supportFragmentManager, "dialog")
        }

        binding.arca.setOnClickListener{
            bundle.putString("nombre", "Arca")
            fragB.arguments = bundle
            fragB.show(supportFragmentManager, "dialog")
        }

        binding.arqui.setOnClickListener{
            bundle.putString("nombre", "Arqui-diseño")
            fragB.arguments = bundle
            fragB.show(supportFragmentManager, "dialog")
        }

        binding.artes.setOnClickListener{
            bundle.putString("nombre", "Artes")
            fragB.arguments = bundle
            fragB.show(supportFragmentManager, "dialog")
        }

        binding.morePlaces.setOnClickListener{
            ListDialogFragment().show(supportFragmentManager, "dialog")
        }
    }
}