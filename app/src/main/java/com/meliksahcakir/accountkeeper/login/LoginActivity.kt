package com.meliksahcakir.accountkeeper.login

import android.content.Intent
import android.os.Bundle
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
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.utils.Result
import com.meliksahcakir.accountkeeper.utils.afterTextChanged
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGN_IN = 1000

    private val loginViewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory(LoginRepository.getInstance())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()
        loginViewModel.loginPage.observe(this) {
            if (loginViewModel.isLoginPage()) {
                formChangeTextView.setText(R.string.don_t_you_have_an_account_create)
                signInTextView.text = getString(R.string.sign_in)
                forgotPasswordTextView.isVisible = true
            } else {
                formChangeTextView.setText(R.string.i_have_an_account)
                signInTextView.text = getString(R.string.sign_up)
                forgotPasswordTextView.isInvisible = true
            }
        }

        loginViewModel.loginResult.observe(this) {
            updateViews(false)
            when (it) {
                is Result.Success<*> -> Toast.makeText(
                    applicationContext,
                    "Welcome " + it.data,
                    Toast.LENGTH_SHORT
                ).show()
                is Result.Error -> Toast.makeText(
                    applicationContext,
                    it.exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            onUserAcquired()
        }

        formChangeTextView.setOnClickListener {
            loginViewModel.onLoginPageChanged()
        }

        emailEditText.afterTextChanged {
            emailTextInputLayout.error = null
            onDataChanged()
        }

        passwordEditText.afterTextChanged {
            passwordTextInputLayout.error = null
            onDataChanged()
        }

        signInFab.setOnClickListener {
            loginViewModel.loginFormState.value?.let {
                if (it.isDataValid) {
                    updateViews(true)
                    val email = emailEditText.text?.toString() ?: ""
                    val password =  passwordEditText.text?.toString() ?: ""
                    if (loginViewModel.isLoginPage()) {
                        loginViewModel.login(email, password)
                    } else {
                        loginViewModel.signUp(email, password)
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
    }

    private fun showErrors(loginState: LoginFormState) {
        if (loginState.emailError != null) {
            emailTextInputLayout.error = getString(loginState.emailError)
        }
        if (loginState.passwordError != null) {
            passwordTextInputLayout.error = getString(loginState.passwordError)
        }
        if (loginState.resetPasswordStatus != null) {
            Toast.makeText(this, loginState.resetPasswordStatus, Toast.LENGTH_SHORT).show()
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

    override fun finish() {
        firebaseAuth.signOut()
        super.finish()
    }


    private fun onUserAcquired() {
        val user = loginViewModel.getUser()
        user?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun onDataChanged() {
        loginViewModel.onUserInfoChanged(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        )
    }

    private fun updateViews(operating: Boolean) {
        signInFab.isEnabled = !operating
        forgotPasswordTextView.isEnabled = !operating
        formChangeTextView.isEnabled = !operating
        loading.isVisible = operating
    }
}
