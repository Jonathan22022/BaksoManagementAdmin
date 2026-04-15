package com.example.baksomanagementadmin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.baksomanagementadmin.data.repository.AuthRepository
import com.example.baksomanagementadmin.ui.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HomepageActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navigationView = findViewById<NavigationView>(R.id.navigationDrawer)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        // default fragment
        if (savedInstanceState == null) {
            loadFragment(HomepageFragment())
        }

        // BOTTOM NAVIGATION
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> loadFragment(HomepageFragment())
                R.id.menu_makanan -> loadFragment(MenuFragment())
                R.id.menu_cart -> loadFragment(CartFragment())
                R.id.menu_account -> loadFragment(AccountFragment())
            }
            true
        }

        // DRAWER MENU
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_dashboard -> loadFragment(HomepageFragment())
                R.id.menu_search -> loadFragment(SearchFragment())
                R.id.menu_add_makanan -> loadFragment(AddMenuFragment())
                R.id.menu_history -> loadFragment(HistoryFragment())
                R.id.menu_favourite -> loadFragment(FavouriteFragment())
                R.id.menu_about_us -> loadFragment(AboutUsFragment())
                R.id.menu_setting -> loadFragment(SettingFragment())
                R.id.menu_logout -> {
                    authRepository.logout()
                    val intent = Intent(this, MainActivity::class.java) // activity yg ada FirstPageFragment
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.close()
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, fragment)
            .commit()
    }

}