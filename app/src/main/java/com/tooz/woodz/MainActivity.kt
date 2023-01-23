package com.tooz.woodz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tooz.woodz.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        setupActionBarWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.camera -> {
                        val intent = Intent(this, BarcodeScannerActivity::class.java)
                        Log.i("ScanCallback", "before barcode activity beaconAddress: {$nearestBeaconAddress}")
                        intent.putExtra("beaconAddress", nearestBeaconAddress)
                        startActivity(intent)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        val search = menu.findItem(R.id.search_bar)
        val searchView = search?.actionView as SearchView

        searchView.queryHint = "Search"
        searchView.maxWidth = Integer.MAX_VALUE

        return super.onCreateOptionsMenu(menu)
    }
}
