package com.example.wastemanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

private val homeFragment = HomeFragment()
private val mapsFragment = MapFragment()


class mainScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        replaceFragment(homeFragment)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener{
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_map -> replaceFragment(mapsFragment)
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
}