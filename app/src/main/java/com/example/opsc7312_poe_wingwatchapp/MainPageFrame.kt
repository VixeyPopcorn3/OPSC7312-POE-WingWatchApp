package com.example.opsc7312_poe_wingwatchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainPageFrame : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val profandSetFragment = ProfandSetFragment()
    private val observationsFragment = ObservationsFragment()
    private val mapFragment = MapFragment()
    private val comandAchieFragment = ComandAchieFragment()
    private val journalFragment = JournalFragment()

    private lateinit var loginId: String // Declare loginId as a lateinit var

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page_frame)

        // Retrieve the loginId extra from the intent
        loginId = intent.getStringExtra("loginId").toString()

        val LogOutbtn = findViewById<Button>(R.id.LogOutBtn)

        LogOutbtn.setOnClickListener()
        {
            startActivity(Intent(this@MainPageFrame, StartPage::class.java))
        }

        switchFragment(profandSetFragment)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        val toggleButton: ImageButton = findViewById(R.id.toggle_button)


        // Inflate the custom header layout
        val headerView = layoutInflater.inflate(R.layout.nav_header, null)

        // Add the custom header view to the NavigationView
        navigationView.addHeaderView(headerView)

        toggleButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ProfandSet -> {
                    switchFragment(profandSetFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_Observation -> {
                    switchFragment(observationsFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_Map -> {
                    switchFragment(mapFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_ComandAchie -> {
                    switchFragment(comandAchieFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_Journal -> {
                    switchFragment(journalFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_ProfandSet -> {
                switchFragment(profandSetFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_Observation -> {
                switchFragment(observationsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_Map -> {
                switchFragment(mapFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_ComandAchie -> {
                switchFragment(comandAchieFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_Journal -> {
                switchFragment(journalFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun switchFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("loginId", loginId) //for login details linked idk if you gonna use this
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.contentLayout, fragment)
            .commit()
    }
}