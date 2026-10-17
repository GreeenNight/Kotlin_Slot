package com.footballkick.project12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.footballkick.project12.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_menu)



        binding.button1.setOnClickListener{
            startActivity(Intent(this, GameActivity::class.java))
        }
        binding.button3.setOnClickListener{
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

}