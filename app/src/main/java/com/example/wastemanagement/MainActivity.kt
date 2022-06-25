package com.example.wastemanagement

import android.app.ActionBar
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var onboardingItemAdapter: OnboardingItemAdapter
    private lateinit var indicatorContainer : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setOnboardingItem()
        setupIndicator()
        setCurrentIndicator(0)
    }

    private fun setOnboardingItem(){
        onboardingItemAdapter = OnboardingItemAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.drawable.a,
                    title = "Manage",
                    description = "djjkahdjkahdjhdjbb dhg hag d djag iuadjshjghjgd "
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.b,
                    title = "Manage",
                    description = "djjkahdjkahdjhdjbb dhg hag d djag iuadjshjghjgd "
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.c,
                    title = "Manage",
                    description = "djjkahdjkahdjhdjbb dhg hag d djag iuadjshjghjgd "
                )
            )
        )

        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewpage)
        onboardingViewPager.adapter = onboardingItemAdapter
        onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
            })
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER


        findViewById<ImageView>(R.id.nextImage).setOnClickListener{
            if(onboardingViewPager.currentItem+1 < onboardingItemAdapter.itemCount){
                onboardingViewPager.currentItem += 1
            }else{
                navigateToDashboard()
            }
        }

        findViewById<MaterialButton>(R.id.btngetstart).setOnClickListener{
            navigateToDashboard()
        }

        findViewById<TextView>(R.id.skip).setOnClickListener{
            navigateToDashboard()
        }

    }

    private fun setupIndicator(){
        indicatorContainer = findViewById(R.id.indicatorContainer)
        val indicators = arrayOfNulls<ImageView>(onboardingItemAdapter.itemCount)
        val layoutParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for(i in indicators.indices){
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let{
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
    }
    private fun navigateToDashboard(){
        startActivity(Intent(this,SingInActivity::class.java))
        finish()
    }
    private fun setCurrentIndicator(position:Int){
        val childcount = indicatorContainer.childCount
        for(i in 0 until childcount){
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            if(i == position){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_background
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_background
                    )
                )
            }
        }
    }
}