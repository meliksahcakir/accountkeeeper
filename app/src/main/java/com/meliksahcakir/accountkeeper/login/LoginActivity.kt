package com.meliksahcakir.accountkeeper.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.utils.EventObserver
import com.meliksahcakir.accountkeeper.utils.Result
import com.meliksahcakir.accountkeeper.utils.afterTextChanged
import com.meliksahcakir.accountkeeper.utils.moveCursorToEnd
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

private const val GOOGLE_SIGN_IN = 1000

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val loginViewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory(
            LoginRepository.getInstance(),
            (application as AccountKeeperApplication).accountRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()
        loginViewModel.loginPage.observe(this) {
            if (loginViewModel.isLoginPage()) {
                formChangeTextView.setText(R.string.don_t_you_have_an_account_create)
                signInTextView.text = getString(R.string.sign_in)
                userNameCardView.isVisible = false
                forgotPasswordTextView.isVisible = true
            } else {
                formChangeTextView.setText(R.string.i_have_an_account)
                signInTextView.text = getString(R.string.sign_up)
                userNameCardView.isVisible = true
                forgotPasswordTextView.isInvisible = true
            }
        }

        loginViewModel.loginResult.observe(this) {
            updateViews(false)
            when (it) {
                is Result.Success -> Toast.makeText(
                    applicationContext,
                    "Welcome " + it.data.uid,
                    Toast.LENGTH_SHORT
                ).show()
                is Result.Error -> Toast.makeText(
                    applicationContext,
                    it.exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            onUserAcquired(true)
        }

        loginViewModel.passwordVisibility.observe(this) {
            if (it) {
                passwordToggleImageView.setImageResource(R.drawable.ic_visibility_off)
                passwordEditText.transformationMethod = null
            } else {
                passwordToggleImageView.setImageResource(R.drawable.ic_visibility_on)
                passwordEditText.transformationMethod = PasswordTransformationMethod()
            }
            passwordEditText.moveCursorToEnd()
        }

        loginViewModel.navigateToMainActivity.observe(this, EventObserver {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })

        formChangeTextView.setOnClickListener {
            loginViewModel.onLoginPageChanged()
        }

        emailEditText.afterTextChanged {
            updateEmailErrorStatus("")
            onDataChanged()
        }

        passwordEditText.afterTextChanged {
            updatePasswordErrorStatus("")
            onDataChanged()
        }
        userNameEditText.afterTextChanged {
            updateUserNameErrorStatus("")
            onDataChanged()
        }

        signInFab.setOnClickListener {
            loginViewModel.loginFormState.value?.let {
                if (it.isDataValid) {
                    updateViews(true)
                    val email = emailEditText.text?.toString() ?: ""
                    val password = passwordEditText.text?.toString() ?: ""
                    if (loginViewModel.isLoginPage()) {
                        loginViewModel.login(email, password)
                    } else {
                        val userName = userNameEditText.text?.toString() ?: ""
                        loginViewModel.signUp(email, password, userName)
                    }
                } else {
                    showErrors(it)
                }
            }
        }

        forgotPasswordTextView.setOnClickListener {
            var email = emailEditText.text?.toString() ?: ""
            email = email.trim()
            loginViewModel.onForgotPasswordClicked(email)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInButton.setOnClickListener {
            googleSignIn()
        }

        passwordToggleImageView.setOnClickListener {
            loginViewModel.togglePasswordVisibility()
        }
    }

    private fun showErrors(loginState: LoginFormState) {
        if (loginState.emailError != null) {
            updateEmailErrorStatus(getString(loginState.emailError))
        }
        if (loginState.passwordError != null) {
            updatePasswordErrorStatus(getString(loginState.passwordError))
        }
        if (loginState.resetPasswordStatus != null) {
            Toast.makeText(this, loginState.resetPasswordStatus, Toast.LENGTH_SHORT).show()
        }
        if (loginState.userNameError != null) {
            updateUserNameErrorStatus(getString(R.string.required_field))
        }
    }

    private fun googleSignIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            data?.let {
                updateViews(true)
                loginViewModel.login(data)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        onUserAcquired()
    }

    private fun onUserAcquired(userRequested: Boolean = false) {
        val user = loginViewModel.getUser()
        user?.let {
            Timber.d("uid = ${it.uid}")
            loginViewModel.initializeUserAccounts(userRequested)
        }
    }

    private fun onDataChanged() {
        loginViewModel.onUserInfoChanged(
            emailEditText.text.toString(),
            passwordEditText.text.toString(),
            userNameEditText.text.toString()
        )
    }

    private fun updateViews(operating: Boolean) {
        signInFab.isEnabled = !operating
        forgotPasswordTextView.isEnabled = !operating
        formChangeTextView.isEnabled = !operating
        loading.isVisible = operating
    }

    private fun updateEmailErrorStatus(error: String) {
        emailErrorImageView.isVisible = error != ""
        emailErrorTextView.isInvisible = error == ""
        emailErrorTextView.text = error
    }

    private fun updatePasswordErrorStatus(error: String) {
        passwordErrorImageView.isVisible = error != ""
        passwordToggleImageView.isVisible = error == ""
        passwordErrorTextView.isInvisible = error == ""
        passwordErrorTextView.text = error
    }

    private fun updateUserNameErrorStatus(error: String) {
        userNameErrorImageView.isVisible = error != ""
        userNameErrorTextView.isInvisible = error == ""
        userNameErrorTextView.text = error
    }
}
