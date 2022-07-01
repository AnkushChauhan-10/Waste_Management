package com.example.wastemanagement

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.audiofx.Equalizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private val homeFragment = HomeFragment()
private val mapsFragment = MapFragment()


class mainScreen : AppCompatActivity() {

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var menue_draw_view : NavigationView
    private lateinit var db : FirebaseFirestore
    private lateinit var userId : String
    private lateinit var sharedPref : SharedPreferences

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkUser()
        drawer()
    }

    private fun checkUser(){
        sharedPref=this?.getPreferences(Context.MODE_PRIVATE)?:return
        val isLogin=sharedPref.getString("Email","1")
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

            findViewById<TextView>(R.id.personName).text = result.get("name").toString()
            findViewById<TextView>(R.id.personEmail).text = result.get("email").toString()
           // findViewById<TextView>(R.id.phoneDB).text = result.get("phone_no").toString()
           // findViewById<TextView>(R.id.addressDB).text = result.get("address").toString()

        }.addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error getting documents.", exception)
        }
    }

    private fun drawer(){
        replaceFragment(homeFragment)

        drawerLayout = findViewById(R.id.drawer_layout)
        menue_draw_view = findViewById<NavigationView>(R.id.menuDrawer_view)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener{
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_map -> getCurrentLocation()//replaceFragment(mapsFragment)
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
    private fun replaceFragment(fragment:Fragment){
        if(fragment!=null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.screenFrameLayout,fragment)
            transaction.commit()
        }
    }

    companion object {
        private const val PERMMISSION_REQUEST = 1
    }

    private fun getCurrentLocation(){
        if(checkPermissions()){
            if(isLocationEnable()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
                    val location:Location?=task.result
                    if(location == null){
                        //---------------
                    }else{
                        Toast.makeText(this,""+location.latitude+"="+location.longitude,Toast.LENGTH_SHORT).show()
                    }
                }
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
                Toast.makeText(this,"dddddd",Toast.LENGTH_SHORT).show()
            }
        }
    }


}