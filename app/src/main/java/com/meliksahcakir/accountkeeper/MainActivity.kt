package com.meliksahcakir.accountkeeper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.meliksahcakir.accountkeeper.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var host: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        host = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        bottomNavigationView.setupWithNavController(host.navController)
        host.navController.addOnDestinationChangedListener(this)
        (application as AccountKeeperApplication).preferenceRepository.nightMode.observe(this) {
            AppCompatDelegate.setDefaultNightMode(it)
        }
        if (intent != null) {
            handleDeepLinkIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleDeepLinkIntent(it)
        }
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

    private fun handleDeepLinkIntent(intent: Intent) {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                handleNavigation(deepLink)
            }
            .addOnFailureListener(this) { e -> Timber.d("getDynamicLink:onFailure: ${e.printStackTrace()}") }
    }

    private fun handleNavigation(uri: Uri?) {
        val app = application as AccountKeeperApplication
        if (app.accountRepository.getUserId() == "") {
            app.unhandledDeepLink = uri
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            val link = uri ?: app.unhandledDeepLink
            app.unhandledDeepLink = null
            link?.let {
                host.navController.safeNavigateToDeepLink(it)
            }
        }
    }
}

fun NavController.safeNavigateToDeepLink(uri: Uri) {
    try {
        if (graph.hasDeepLink(uri)) {
            navigate(uri)
        } else {
            safeNavigateTo(R.id.personalAccountsFragment)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun NavController.safeNavigateTo(id: Int) {
    val action = currentDestination?.getAction(id)
    action?.let { navigate(id) }
}

