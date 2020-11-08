package com.meliksahcakir.accountkeeper.login

import android.content.Intent
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.utils.Result
import com.meliksahcakir.accountkeeper.utils.isEmailValid
import com.meliksahcakir.accountkeeper.utils.isPasswordValid
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<Result<FirebaseUser>>()
    val loginResult: LiveData<Result<FirebaseUser>> = _loginResult

    private val _loginPage = MutableLiveData<Boolean>(true)
    val loginPage: LiveData<Boolean> = _loginPage

    private val _passwordVisibility = MutableLiveData<Boolean>(false)
    val passwordVisibility: LiveData<Boolean> = _passwordVisibility

    fun onLoginPageChanged() {
        _loginPage.value = _loginPage.value != true
    }

    fun isLoginPage(): Boolean {
        return _loginPage.value ?: true
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = loginRepository.login(email, password)
            _loginResult.value = result
        }
    }

    fun login(data: Intent) {
        viewModelScope.launch {
            val result = loginRepository.login(data)
            _loginResult.value = result
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            val result = loginRepository.signUp(email, password)
            _loginResult.value = result
        }
    }

    fun onUserInfoChanged(email: String, password: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun onForgotPasswordClicked(email: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else {
            viewModelScope.launch {
                val result = loginRepository.sendPasswordResetEmail(email)
                if (result is Result.Success) {
                    _loginForm.value =
                        LoginFormState(resetPasswordStatus = R.string.password_reset_email_sent)
                } else {
                    _loginForm.value =
                        LoginFormState(resetPasswordStatus = R.string.password_reset_email_error)
                }
            }
        }
    }

    fun getUser(): FirebaseUser? {
        return loginRepository.getUser()
    }

    fun togglePasswordVisibility() {
        val visibility = _passwordVisibility.value ?: false
        _passwordVisibility.value = !visibility
    }

    fun initializeUserAccounts() {
        loginRepository.getUser()?.let {
            accountRepository.setUserId(it.uid)
            viewModelScope.launch {
                accountRepository.refreshAccounts()
            }
        }
    }
}

class LoginViewModelFactory(
    private val loginRepository: LoginRepository,
    private val accountRepository: AccountRepository
) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginRepository, accountRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}