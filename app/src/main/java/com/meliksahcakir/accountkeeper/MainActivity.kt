package com.meliksahcakir.accountkeeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.meliksahcakir.accountkeeper.utils.ExitWithAnimation
import com.meliksahcakir.accountkeeper.utils.color
import com.meliksahcakir.accountkeeper.utils.exitCircularReveal
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var host: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        host = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        bottomNavigationView.setupWithNavController(host.navController)
        host.navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.personalAccountsFragment -> mainFab.setImageResource(R.drawable.ic_add)
            R.id.friendAccountsFragment -> mainFab.setImageResource(R.drawable.ic_add)
            R.id.findAccountsFragment -> mainFab.setImageResource(R.drawable.ic_search)
            R.id.settingsFragment -> mainFab.setImageResource(R.drawable.ic_home)
            R.id.addUpdateAccountFragment -> mainFab.setImageResource(R.drawable.ic_save)
        }
    }
}
