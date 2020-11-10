package com.meliksahcakir.accountkeeper.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.utils.Event
import com.meliksahcakir.accountkeeper.utils.SnackBarParameters
import com.meliksahcakir.accountkeeper.utils.createDynamicLinkForTheAccountList
import com.meliksahcakir.accountkeeper.utils.share
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsViewModel(private val repository: AccountRepository) : ViewModel() {

    private val _navigateToLoginScreen = MutableLiveData<Event<Unit>>()
    val navigateToLoginScreen: LiveData<Event<Unit>> = _navigateToLoginScreen

    private val _syncBusy = MutableLiveData<Boolean>(false)
    val syncBusy: LiveData<Boolean> = _syncBusy

    private val _snackBarParams = MutableLiveData<Event<SnackBarParameters>>()
    val snackBarParams: LiveData<Event<SnackBarParameters>> = _snackBarParams

    fun onSignOutButtonClicked() {
        FirebaseAuth.getInstance().signOut()
        _navigateToLoginScreen.value = Event(Unit)
    }

    fun onSyncButtonClicked() {
        _syncBusy.value = true
        viewModelScope.launch {
            repository.refreshAccounts()
            _snackBarParams.value = Event(SnackBarParameters(R.string.accounts_synced_successfully))
            _syncBusy.value = false
        }
    }

    fun onShareProfileButtonClicked(activityContext: Context) {
        viewModelScope.launch {
            try {
                val link = createDynamicLinkForTheAccountList(repository.getUserId()).await()
                val uriString = link.shortLink.toString()
                val message =
                    activityContext.getString(R.string.click_the_link_for_available_accounts) + "\n" + uriString
                activityContext.share(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

