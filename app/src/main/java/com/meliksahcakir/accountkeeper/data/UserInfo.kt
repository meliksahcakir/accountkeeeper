package com.meliksahcakir.accountkeeper.data

import com.google.firebase.firestore.Exclude

data class UserInfo(
    val username: String = "",
    val email: String = "",
    @get:Exclude
    var uid: String = ""
)