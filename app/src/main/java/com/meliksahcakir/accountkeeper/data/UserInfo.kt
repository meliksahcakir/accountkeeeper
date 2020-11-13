package com.meliksahcakir.accountkeeper.data

import com.google.firebase.firestore.Exclude
import java.util.*

data class UserInfo(
    val username: String = "",
    val email: String = "",
    @get:Exclude
    var uid: String = "",
    var usernameLowerCase: String = ""
) {
    init {
        usernameLowerCase = username.toLowerCase(Locale.getDefault())
    }
}