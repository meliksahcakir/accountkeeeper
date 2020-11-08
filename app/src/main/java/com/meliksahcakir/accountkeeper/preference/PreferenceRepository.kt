package com.meliksahcakir.accountkeeper.preference

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PreferenceRepository(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val PREF_NIGHT_MODE = "pref_night_mode"
        private const val PREF_NIGHT_MODE_DEFAULT = AppCompatDelegate.MODE_NIGHT_NO
    }

    val nightModePref: Int
        get() = sharedPreferences.getInt(PREF_NIGHT_MODE, PREF_NIGHT_MODE_DEFAULT)

    private val _nightMode = MutableLiveData<Int>()
    val nightMode: LiveData<Int> = _nightMode

    var isDarkThemeSelected = false
        get() = nightModePref == AppCompatDelegate.MODE_NIGHT_YES
        set(value) {
            field = value
            sharedPreferences.edit()
                .putInt(
                    PREF_NIGHT_MODE,
                    if (value) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                ).apply()
        }

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                PREF_NIGHT_MODE -> {
                    _nightMode.value = nightModePref
                    _isDarkTheme.value = isDarkThemeSelected
                }
            }
        }

    init {
        _nightMode.value = nightModePref
        _isDarkTheme.value = isDarkThemeSelected
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}