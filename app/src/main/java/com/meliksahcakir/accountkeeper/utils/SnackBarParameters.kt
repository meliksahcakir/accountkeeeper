package com.meliksahcakir.accountkeeper.utils

import androidx.annotation.StringRes

enum class SnackBarAction {
    NONE,
    OPEN,
    SAVE,
    SHARE,
    UNDO,
    RETRY,
    CANCEL,
    SKIP,
    OK
}

data class SnackBarParameters(
    @StringRes
    val messageStringId: Int,
    val data: Any? = null,
    val action: SnackBarAction = SnackBarAction.NONE,
    @StringRes
    val actionStringId: Int = 0
)