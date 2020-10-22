package com.meliksahcakir.accountkeeper.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val resetPasswordStatus: Int? = null,
    val isDataValid: Boolean = false
)
