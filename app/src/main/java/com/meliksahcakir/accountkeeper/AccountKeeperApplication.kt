package com.meliksahcakir.accountkeeper

import android.app.Application
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.utils.ServiceLocator

class AccountKeeperApplication: Application() {

    lateinit var accountRepository: AccountRepository
        private set

    override fun onCreate() {
        super.onCreate()
        accountRepository = ServiceLocator.provideAccountRepository(this)
    }
}