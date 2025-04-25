package com.example.fdetection

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomepageFragment())
            .commit()

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_stepsleep -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, StepSleepFragment())
                        .commit()
                    true
                }
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomepageFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
