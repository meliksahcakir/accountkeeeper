package com.meliksahcakir.accountkeeper.settings

import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.login.LoginRepository
import com.meliksahcakir.accountkeeper.login.LoginViewModel
import com.meliksahcakir.accountkeeper.utils.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsViewModel(
    private val loginRepository: LoginRepository,
    private val repository: AccountRepository
) : ViewModel() {

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

    fun onChangePasswordButtonClicked() {
        val email = loginRepository.getUser()?.email
        if (email != null) {
            viewModelScope.launch {
                val result = loginRepository.sendPasswordResetEmail(email)
                if (result is Result.Success) {
                    _snackBarParams.value =
                        Event(SnackBarParameters(R.string.password_reset_email_sent))
                } else {
                    _snackBarParams.value =
                        Event(SnackBarParameters(R.string.password_reset_email_error))
                }
            }
        }
    }
}

class SettingsViewModelFactory(
    private val loginRepository: LoginRepository,
    private val accountRepository: AccountRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(loginRepository, accountRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

