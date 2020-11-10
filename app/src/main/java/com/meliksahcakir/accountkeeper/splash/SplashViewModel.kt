package com.meliksahcakir.accountkeeper.splash

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.utils.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SplashActivityDirections {
    LOGIN_ACTIVITY,
    MAIN_ACTIVITY
}

class SplashViewModel(private val repository: AccountRepository) : ViewModel() {

    private var user: FirebaseUser? = null
    private var initialized = false

    private val _navigateToNextScreen = MutableLiveData<Event<SplashActivityDirections>>()
    val navigateToNextScreen: LiveData<Event<SplashActivityDirections>> = _navigateToNextScreen

    init {
        user = FirebaseAuth.getInstance().currentUser
        start()
        val direction: SplashActivityDirections =
            if (user != null) SplashActivityDirections.MAIN_ACTIVITY else SplashActivityDirections.LOGIN_ACTIVITY
        viewModelScope.launch {
            delay(2000)
            _navigateToNextScreen.postValue(Event(direction))
        }
    }

    private fun start() {
        if (!initialized && user != null) {
            repository.setUserId(user!!.uid)
            viewModelScope.launch {
                repository.refreshAccounts()
            }
            initialized = true
        }
    }
}
