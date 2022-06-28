package com.example.wastemanagement

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SingUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        // Initialize Firebase Auth-----------------------------------------------------
        auth = Firebase.auth

        //singIn-----------------------------------------------------------------------
        findViewById<MaterialButton>(R.id.registerButton).setOnClickListener{
            //variable --------------------------------------------------------------------
            val email = findViewById<EditText>(R.id.emailSingUp).text.toString()
            val name = findViewById<EditText>(R.id.nameSingUp).text.toString()
            val password = findViewById<EditText>(R.id.passwordSingUp).text.toString()
            val address = findViewById<EditText>(R.id.addressSingUp).text.toString()
            val phone_no = findViewById<EditText>(R.id.phoneSingUp).text.toString()

             auth.createUserWithEmailAndPassword(email,password)
                 .addOnCompleteListener(this) { task ->
                     if (task.isSuccessful) {
                         Toast.makeText(baseContext, "Authentication yes.",
                             Toast.LENGTH_SHORT).show()
                     } else {
                         // If sign in fails, display a message to the user.
                         Log.w(TAG, "createUserWithEmail:failure", task.exception)
                         Toast.makeText(baseContext, "Authentication failed."+email+"er"+task.exception,
                             Toast.LENGTH_SHORT).show()

                     }
                 }
        }

    }
}