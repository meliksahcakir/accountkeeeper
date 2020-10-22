package com.meliksahcakir.accountkeeper.utils

import android.content.Context
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.data.local.AccountDatabase
import com.meliksahcakir.accountkeeper.data.local.AccountLocalDataSource
import com.meliksahcakir.accountkeeper.data.remote.AccountRemoteDataSource

object ServiceLocator {

    @Volatile
    private var accountRepository: AccountRepository? = null

    fun provideAccountRepository(context: Context): AccountRepository {
        synchronized(this) {
            return accountRepository ?: createAccountRepository(context)
        }
    }

    private fun createAccountRepository(context: Context): AccountRepository {
        val source = AccountLocalDataSource(AccountDatabase.getInstance(context).accountDao)
        val repository = AccountRepository(source, AccountRemoteDataSource)
        accountRepository = repository
        return repository
    }
}