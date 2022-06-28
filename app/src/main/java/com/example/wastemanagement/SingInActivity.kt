package com.example.wastemanagement

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SingInActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)
        (findViewById<TextView>(R.id.singUpText)).setOnClickListener {
            startActivity(Intent(this,SingUpActivity::class.java))
            finish()
        }
        //Initialize Firebase Auth---------------------------------------------------
        auth= Firebase.auth

        findViewById<MaterialButton>(R.id.sinInButton).setOnClickListener{

            val email = findViewById<EditText>(R.id.email).text.toString()
            val password =  findViewById<EditText>(R.id.password).text.toString()

            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@SingInActivity,boardActivity::class.java))
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed."+task.exception.toString(),
                            Toast.LENGTH_SHORT).show()
                        print(task.exception)
                    }

                }
        }
    }
}