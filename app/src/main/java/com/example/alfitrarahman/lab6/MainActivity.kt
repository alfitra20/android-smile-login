package com.example.alfitrarahman.lab6

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logout.setOnClickListener{
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
