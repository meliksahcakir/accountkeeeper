package com.meliksahcakir.accountkeeper.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meliksahcakir.accountkeeper.addupdate.AddUpdateAccountViewModel
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.friend.FriendAccountsViewModel
import com.meliksahcakir.accountkeeper.personal.PersonalAccountsViewModel
import com.meliksahcakir.accountkeeper.settings.SettingsViewModel
import com.meliksahcakir.accountkeeper.splash.SplashViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: AccountRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(PersonalAccountsViewModel::class.java) ->
                    PersonalAccountsViewModel(repository)
                isAssignableFrom(FriendAccountsViewModel::class.java) ->
                    FriendAccountsViewModel(repository)
                isAssignableFrom(SettingsViewModel::class.java) ->
                    SettingsViewModel(repository)
                isAssignableFrom(SplashViewModel::class.java) ->
                    SplashViewModel(repository)
                isAssignableFrom(AddUpdateAccountViewModel::class.java) ->
                    AddUpdateAccountViewModel(repository)
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }
}