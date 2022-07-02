package com.example.wastemanagement

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.wastemanagement.databinding.ActivityMapsBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var mark:MarkerOptions
    private var m : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        databaseRef=
            FirebaseDatabase.getInstance("https://waste-b6bcb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Locations")
        databaseRef.addValueEventListener(logListener)

    }

    val logListener = object :ValueEventListener{
        override fun onCancelled(error: DatabaseError) {

        }

        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists()){
                val locationlogging = snapshot.child("adminLocation").getValue(userLocation::class.java)
                var adminLat = locationlogging?.latitude
                var adminLng = locationlogging?.longitude

                if(adminLat!=null && adminLng!=null){
                    var adminLoc = LatLng(adminLat,adminLng)
                    if(m){
                        mark.position(adminLoc)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(adminLoc,18f))
                    }else{
                        mark=MarkerOptions().position(adminLoc).title("Truck")
                        mMap.addMarker(mark)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(adminLoc,15f))
                        m=true
                    }
                }

            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,mainScreen::class.java))
        finish()
    }
}