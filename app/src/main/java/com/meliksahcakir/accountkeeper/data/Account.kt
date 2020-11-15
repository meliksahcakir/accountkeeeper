package com.meliksahcakir.accountkeeper.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "accounts")
data class Account(
    @ColumnInfo(name = "accountName")
    var accountName: String = "",
    @ColumnInfo(name = "accountNumber")
    var accountNumber: String = "",
    @ColumnInfo(name = "accountDescription")
    var accountDescription: String = "",
    @ColumnInfo(name = "personalAccount")
    var personalAccount: Boolean = true,
    @ColumnInfo(name = "global")
    var global: Boolean = false,
    @ColumnInfo(name = "accountType")
    var accountType: Int = BANK_ACCOUNT,
    @ColumnInfo(name = "userId")
    var userId: String = "",
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "accountId")
    var accountId: String = UUID.randomUUID().toString()

) : Parcelable {
    companion object {
        const val BANK_ACCOUNT = 0
        const val CRYPTO = 1
        const val EMAIL = 2
        const val PHONE = 3
        const val LOCATION = 4
        const val OTHER = 5
    }

    fun update(
        name: String,
        number: String,
        desc: String,
        type: Int,
        personal: Boolean,
        global: Boolean
    ) {
        this.accountName = name
        this.accountNumber = number
        this.accountDescription = desc
        this.accountType = type
        this.personalAccount = personal
        this.global = global
    }
}
