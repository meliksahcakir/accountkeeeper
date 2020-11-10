package com.meliksahcakir.accountkeeper.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.login.LoginActivity
import com.meliksahcakir.accountkeeper.utils.EventObserver

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashViewModel> {
        SplashViewModelFactory((application as AccountKeeperApplication).accountRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel.navigateToNextScreen.observe(this, EventObserver {
            if (it == SplashActivityDirections.LOGIN_ACTIVITY) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else if (it == SplashActivityDirections.MAIN_ACTIVITY) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }
}