package com.example.wastemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SingInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)
        (findViewById<TextView>(R.id.singUpText)).setOnClickListener {
            startActivity(Intent(this,SingUpActivity::class.java))
            finish()
        }
    }
}