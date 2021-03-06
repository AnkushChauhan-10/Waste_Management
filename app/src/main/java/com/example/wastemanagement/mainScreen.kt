package com.example.wastemanagement

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.location.LocationManager
import android.media.audiofx.Equalizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.BoringLayout
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenuView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng

class mainScreen : AppCompatActivity() {

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var menue_draw_view : NavigationView
    private lateinit var db : FirebaseFirestore
    private lateinit var sharedPref : SharedPreferences
    private lateinit var dataBaseReference: DatabaseReference
    private  var isLocation : Boolean = false
    private var point : Int = 0


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkUser()
        drawer()
        findViewById<Button>(R.id.submit).setOnClickListener {
            if(isLocation){
                saveLocation()
            }else {
                getCurrentLocation()
            }
        }
    }

    private fun checkUser(){
        sharedPref=this?.getPreferences(Context.MODE_PRIVATE)?:return
        val isLogin=sharedPref.getString("Email","1")
        if(isLogin=="1")
        {
            var email=intent.getStringExtra("email")
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

            findViewById<TextView>(R.id.personName).text = result.get("name").toString()
            findViewById<TextView>(R.id.personEmail).text = result.get("email").toString()
            findViewById<TextView>(R.id.point).text = result.get("point").toString()
            // findViewById<TextView>(R.id.phoneDB).text = result.get("phone_no").toString()
            // findViewById<TextView>(R.id.addressDB).text = result.get("address").toString()

        }.addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error getting documents.", exception)
        }
    }

    private fun drawer(){

        drawerLayout = findViewById(R.id.drawer_layout)
        menue_draw_view = findViewById<NavigationView>(R.id.menuDrawer_view)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener{
            when(it.itemId){
                // R.id.ic_home ->
                R.id.ic_map -> {

                    if(!isLocation){
                        getCurrentLocation()
                    } else{
                        startActivity(Intent(this,MapsActivity::class.java))
                        finish()
                    }
                }
                R.id.ic_menu -> drawerLayout.openDrawer(GravityCompat.END)
            }
            true
        }
        menue_draw_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_logout -> {
                    sharedPref.edit().remove("Email").apply()
                    var intent = Intent(this@mainScreen,SingInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.ic_setting -> {
                    var intent = Intent(this@mainScreen,boardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }

    companion object {
        private const val PERMMISSION_REQUEST = 1
    }

    private fun getCurrentLocation(){
        if(checkPermissions()){
            if(isLocationEnable()){
                isLocation=true
            }else{
                Toast.makeText(this,"Turn on location",Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            requestPermissions()
        }
    }

    private fun isLocationEnable():Boolean{
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMMISSION_REQUEST
        )
    }

    private fun checkPermissions():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode != PERMMISSION_REQUEST){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"granted",Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(this,"denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLocation(){
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
            val location:Location?=task.result
            if(location == null){
                //---------------
            }else{
                val wasteType = findViewById<EditText>(R.id.wasteType).text.toString()
                val wasteWeight= findViewById<EditText>(R.id.wasteWeight).text.toString().toFloat()
                val email = findViewById<MaterialTextView>(R.id.personEmail).text.toString().toString()
                val user = userLocation(location.latitude, location.longitude,wasteType,wasteWeight)
                val name= findViewById<MaterialTextView>(R.id.personName).text.toString()
                val point = (findViewById<TextView>(R.id.point).text.toString().toInt()+wasteWeight).toInt()
                dataBaseReference =
                    FirebaseDatabase.getInstance("https://waste-b6bcb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("usersLocation")
                dataBaseReference.child(name).setValue(user)
                    .addOnSuccessListener {
                        db = Firebase.firestore
                        db.collection("users").document(email).update("point",point)
                        // Toast.makeText(this, "Save Data", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        //Toast.makeText(this, "No database", Toast.LENGTH_SHORT).show()
                    }
                //Toast.makeText(this,""+location.latitude+"="+location.longitude,Toast.LENGTH_SHORT).show()
            }
        }
    }

}