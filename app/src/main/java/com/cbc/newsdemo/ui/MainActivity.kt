package com.cbc.newsdemo.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cbc.newsdemo.R
import com.cbc.newsdemo.data.db.ArticleDatabase
import com.cbc.newsdemo.data.repository.NewsRepository
import com.cbc.newsdemo.databinding.ActivityMainBinding
import com.cbc.newsdemo.utils.CheckConnection
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NewsViewModel
    private var menu: Menu? = null
    private val checkConnection by lazy { CheckConnection(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository= NewsRepository(ArticleDatabase(this))
        viewModel= ViewModelProvider(this, NewsViewModelProviderFactory(application, repository))[NewsViewModel::class.java]

        /*// Calling the support action bar and setting it to custom
        this.supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

        // Displaying the custom layout in the ActionBar
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.actionbar)*/

        val navView: BottomNavigationView = binding.navView

        val navHostFragment= supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController= navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_news, R.id.navigation_saved_news
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        viewModel.connection.observe(this) { isConnected ->
            if (isConnected) {
                menu?.findItem(R.id.connection_status)?.setIcon(R.drawable.ic_connectivity_available)
            } else {
                menu?.findItem(R.id.connection_status)?.setIcon(R.drawable.ic_connectivity_unavailable)
            }
        }

        binding.apply {
            checkConnection.observe(this@MainActivity) {
                if (it) {
                    menu?.findItem(R.id.connection_status)
                        ?.setIcon(R.drawable.ic_connectivity_available)
                } else {
                    menu?.findItem(R.id.connection_status)
                        ?.setIcon(R.drawable.ic_connectivity_unavailable)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

}