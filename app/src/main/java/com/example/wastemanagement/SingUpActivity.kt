package com.example.wastemanagement

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SingUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var  db : FirebaseFirestore
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

            if(check()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = hashMapOf(
                                "name" to name,
                                "phone_no" to phone_no,
                                "address" to address,
                                "email" to email,
                                "password" to password
                            )
                            db = Firebase.firestore
                            db.collection("users")
                                .document(email).set(user)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(TAG, "DocumentSnapshot added with ID:")
                                    startActivity(Intent(this@SingUpActivity, boardActivity::class.java).putExtra("email",email))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed." + email + "er" + task.exception,
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
            }else{
                Toast.makeText(
                    this, "Enter The Details.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun check():Boolean{
        if(findViewById<EditText>(R.id.emailSingUp).text.toString().trim{it<=' '}.isNotEmpty() &&
            findViewById<EditText>(R.id.phoneSingUp).text.toString().trim{it<=' '}.isNotEmpty() &&
            findViewById<EditText>(R.id.passwordSingUp).text.toString().trim{it<=' '}.isNotEmpty() &&
            findViewById<EditText>(R.id.addressSingUp).text.toString().trim{it<=' '}.isNotEmpty() &&
            findViewById<EditText>(R.id.nameSingUp).text.toString().trim{it<=' '}.isNotEmpty()){

            return true
        }
        return false
    }
}