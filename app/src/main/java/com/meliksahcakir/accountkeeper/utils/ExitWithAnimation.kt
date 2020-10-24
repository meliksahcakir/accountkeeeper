package com.meliksahcakir.accountkeeper.utils

interface ExitWithAnimation {
    var posX: Int?
    var posY: Int?

    fun isToBeExitedWithAnimation(): Boolean
}