package com.meliksahcakir.accountkeeper.utils

import androidx.appcompat.app.AppCompatDelegate
import com.chibatching.kotpref.KotprefModel

const val PREF_NIGHT_MODE = "pref_night_mode"

object Preferences : KotprefModel() {
    var userName by stringPref("")
    var userEmail by stringPref("")
    var nightMode by intPref(default = AppCompatDelegate.MODE_NIGHT_NO, key = PREF_NIGHT_MODE)
}