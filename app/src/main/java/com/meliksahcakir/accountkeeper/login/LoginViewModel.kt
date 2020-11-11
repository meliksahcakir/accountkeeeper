package com.meliksahcakir.accountkeeper.login

import android.content.Intent
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.data.UserInfo
import com.meliksahcakir.accountkeeper.utils.*
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

    private var signedInWithGoogle = false
    private var userInfo: UserInfo? = null

    private val _navigateToMainActivity = MutableLiveData<Event<Unit>>()
    val navigateToMainActivity: LiveData<Event<Unit>> = _navigateToMainActivity

    fun onLoginPageChanged() {
        _loginPage.value = _loginPage.value != true
    }

    fun isLoginPage(): Boolean {
        return _loginPage.value ?: true
    }

    fun login(email: String, password: String) {
        userInfo = null
        viewModelScope.launch {
            val result = loginRepository.login(email, password)
            _loginResult.value = result
            signedInWithGoogle = false
        }
    }

    fun login(data: Intent) {
        userInfo = null
        viewModelScope.launch {
            val result = loginRepository.login(data)
            _loginResult.value = result
            signedInWithGoogle = true
        }
    }

    fun signUp(email: String, password: String, userName: String) {
        userInfo =
            UserInfo(userName, email, "")
        viewModelScope.launch {
            val result = loginRepository.signUp(email, password)
            _loginResult.value = result
            signedInWithGoogle = false
        }
    }

    fun onUserInfoChanged(email: String, password: String, userName: String) {
        var valid = true
        var emailError: Int? = null
        var passwordError: Int? = null
        var userNameError: Int? = null

        if (!isEmailValid(email)) {
            emailError = R.string.invalid_email
            valid = false
        }
        if (!isPasswordValid(password)) {
            passwordError = R.string.invalid_password
            valid = false
        }
        if (!isLoginPage() && userName == "") {
            userNameError = R.string.required_field
            valid = false
        }
        _loginForm.value = LoginFormState(emailError, passwordError, null, userNameError, valid)
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

    fun initializeUserAccounts(userRequested: Boolean = false) {
        loginRepository.getUser()?.let {
            accountRepository.setUserId(it.uid)
            viewModelScope.launch {
                if (userRequested) {
                    if (signedInWithGoogle) {
                        val name = it.providerData[0].displayName ?: ""
                        val email = it.providerData[0].email ?: ""
                        userInfo = UserInfo(
                            name,
                            email,
                            ""
                        )
                    }
                    userInfo?.let { user ->
                        Preferences.userName = user.username
                        Preferences.userEmail = user.email
                        accountRepository.updateRemoteUserInfo(user)
                    }
                } else {
                    val result = accountRepository.getRemoteUserInfo()
                    if (result is Result.Success) {
                        val user = result.data
                        if (user.username != "") {
                            Preferences.userName = user.username
                        }
                        if (user.email != "") {
                            Preferences.userEmail = user.email
                        }
                    } else {
                        if (it.displayName != null && it.displayName != "") {
                            Preferences.userName = it.displayName!!
                        }
                        if (it.email != null && it.email != "") {
                            Preferences.userEmail = it.email!!
                        }
                    }
                }
                accountRepository.refreshAccounts()
                _navigateToMainActivity.value = Event(Unit)
            }
        }
    }
}

class LoginViewModelFactory(
    private val loginRepository: LoginRepository,
    private val accountRepository: AccountRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginRepository, accountRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}