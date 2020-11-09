package com.meliksahcakir.accountkeeper.settings

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.utils.Event
import com.meliksahcakir.accountkeeper.utils.SnackBarParameters
import kotlinx.coroutines.launch

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
}

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(
    private val repository: AccountRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return (SettingsViewModel(repository) as T)
    }
}
