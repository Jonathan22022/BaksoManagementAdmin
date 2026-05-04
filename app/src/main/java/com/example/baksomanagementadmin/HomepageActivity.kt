package com.example.baksomanagementadmin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.baksomanagementadmin.data.remote.FirebaseClient
import com.example.baksomanagementadmin.data.repository.AuthRepository
import com.example.baksomanagementadmin.data.repository.UserRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HomepageActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val firestore = FirebaseClient.firestore
    private lateinit var imageViewProfile: ImageView
    private lateinit var tvUserName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navigationView = findViewById<NavigationView>(R.id.navigationDrawer)
        val headerView = navigationView.getHeaderView(0)
        imageViewProfile = headerView.findViewById(R.id.imageViewProfile)
        tvUserName = headerView.findViewById(R.id.tvUserName)
        val navController = supportFragmentManager
            .findFragmentById(R.id.home_fragment_container)
                as NavHostFragment
        val controller = navController.navController
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        // BOTTOM NAVIGATION
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homepageFragment -> controller.navigate(R.id.homepageFragment)
                R.id.menu_bahanBaku -> controller.navigate(R.id.menu_bahanBaku)
                R.id.menu_add_on -> controller.navigate(R.id.menu_add_on)
                R.id.menu_weekly_insight -> controller.navigate(R.id.menu_weekly_insight)
                R.id.menu_account -> controller.navigate(R.id.menu_account)
            }
            true
        }

        // DRAWER MENU
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homepageFragment -> controller.navigate(R.id.homepageFragment)
                R.id.menu_history -> controller.navigate(R.id.menu_history)
                R.id.menu_favourite -> controller.navigate(R.id.menu_favourite)
                R.id.menu_about_us -> controller.navigate(R.id.menu_about_us)
                R.id.menu_setting -> controller.navigate(R.id.menu_setting)
                R.id.menu_logout -> {
                    authRepository.logout()
                    val intent =
                        Intent(this, MainActivity::class.java) // activity yg ada FirstPageFragment
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.close()
            true
        }
        loadUserData()
    }

    private fun loadUserData() {
        userRepository.getCurrentUserData { user ->
            if (user != null) {
                tvUserName.text = user.nama
                val resId = resources.getIdentifier(
                    user.profilePicture,
                    "drawable",
                    packageName
                )

                if (resId != 0) {
                    imageViewProfile.setImageResource(resId)
                } else {
                    imageViewProfile.setImageResource(R.drawable.ic_account_)
                }

                Glide.with(this)
                     .load(user.profilePicture)
                     .into(imageViewProfile)
            }
        }
    }
}