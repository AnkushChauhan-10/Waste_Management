package com.example.wastemanagement

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class boardActivity : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    private lateinit var userId : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        //SharedPre-----------------------------------------------------------
        val sharedPref=this?.getPreferences(Context.MODE_PRIVATE)?:return
        val isLogin=sharedPref.getString("Email","1")
        findViewById<Button>(R.id.btn).setOnClickListener {
            sharedPref.edit().remove("Email").apply()
            var intent = Intent(this@boardActivity,SingInActivity::class.java)
            startActivity(intent)
            finish()
        }

        if(isLogin=="1")
        {
            var email=intent.getStringExtra("email")
            userId = email.toString()
            if(email!=null)
            {
                setText(email)
                with(sharedPref.edit())
                {
                    putString("Email",email)
                    apply()
                }
            }
            else{
                var intent = Intent(this,SingInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }else
        {
            setText(isLogin.toString())
        }

    }
    private fun setText(userId:String){
        db = Firebase.firestore
        db.collection("users").document(userId).get().addOnSuccessListener { result ->

            findViewById<TextView>(R.id.nameDB).text = result.get("name").toString()
            findViewById<TextView>(R.id.emailDB).text = result.get("email").toString()
            findViewById<TextView>(R.id.phoneDB).text = result.get("phone_no").toString()
            findViewById<TextView>(R.id.addressDB).text = result.get("address").toString()

        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents.", exception)
        }
    }

}