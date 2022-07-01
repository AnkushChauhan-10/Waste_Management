package com.example.wastemanagement

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class boardActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var db : FirebaseFirestore
    private lateinit var userId : String
    private lateinit var location:Location
    private lateinit var dataBaseReference: DatabaseReference
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        //SharedPre-----------------------------------------------------------
        val sharedPref=this?.getPreferences(Context.MODE_PRIVATE)?:return
        val isLogin=sharedPref.getString("Email","1")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnMap).setOnClickListener {
            var intent = Intent(this@boardActivity,mainScreen::class.java)
            startActivity(intent)
            finish()
        }

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



    companion object {
        private const val PERMMISSION_REQUEST = 1
    }

    private fun getCurrentLocation(){
        if(checkPermissions()){
            if(isLocationEnable()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
                    val location1:Location?=task.result
                    if(location1 == null){
                        //---------------
                    }else{
                        location=location1
                        Toast.makeText(this,""+location.latitude+"="+location.longitude, Toast.LENGTH_SHORT).show()
                        saveLocation()
                    }
                }
            }else{
                Toast.makeText(this,"Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            requestPermissions()
        }
    }

    private fun isLocationEnable():Boolean{
        val locationManager: LocationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
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
                Toast.makeText(this,"granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(this,"dddddd", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLocation(){

        val user = userLocation(location.latitude, location.longitude)
        dataBaseReference =
            FirebaseDatabase.getInstance("https://waste-b6bcb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Locations")
        dataBaseReference.child("adminLocation").setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "database", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "No database", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        getLocationUpdates()
        startLocation()
    }

    private fun getLocationUpdates(){
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.create().priority
        locationRequest.fastestInterval = 100
        locationRequest.interval = 300

        locationCallback=object : LocationCallback(){
            override fun onLocationResult(locationResult:LocationResult) {
                super.onLocationResult(locationResult)
                if(locationResult.locations.isNotEmpty()){
                    val loc = locationResult.lastLocation
                    if(loc != null){
                        val latLag = LatLng(loc.latitude,loc.longitude)
                        toast(latLag);
                        println("${latLag.latitude} and ${latLag.longitude}")
                        val user = userLocation(latLag.latitude, latLag.longitude)
                        dataBaseReference =
                            FirebaseDatabase.getInstance("https://waste-b6bcb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Locations")
                        dataBaseReference.child("adminLocation").setValue(user)
                            .addOnSuccessListener {

                            }.addOnFailureListener {

                            }
                    }
                }
            }
        }
    }

    private fun startLocation(){
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun toast(location:LatLng){
        Toast.makeText(this,"${location.latitude} and ${location.longitude}",Toast.LENGTH_SHORT).show()
    }

}