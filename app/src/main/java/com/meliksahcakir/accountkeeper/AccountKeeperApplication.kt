package com.meliksahcakir.accountkeeper

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.preference.PreferenceRepository
import com.meliksahcakir.accountkeeper.utils.ServiceLocator
import timber.log.Timber

class AccountKeeperApplication : Application() {

    companion object {
        const val DEFAULT_PREFERENCES = "default+preferences"
    }

    lateinit var accountRepository: AccountRepository
    lateinit var preferenceRepository: PreferenceRepository
        private set

    var unhandledDeepLink: Uri? = null

    override fun onCreate() {
        super.onCreate()
        accountRepository = ServiceLocator.provideAccountRepository(this)
        Timber.plant(Timber.DebugTree())
        preferenceRepository =
            PreferenceRepository((getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE)))
        AppCompatDelegate.setDefaultNightMode(preferenceRepository.nightModePref)
    }
}