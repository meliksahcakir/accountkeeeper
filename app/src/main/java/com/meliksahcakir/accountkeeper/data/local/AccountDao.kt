package com.meliksahcakir.accountkeeper.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.meliksahcakir.accountkeeper.data.Account

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE userId = :uid")
    fun observeAccounts(uid: String): LiveData<List<Account>>

    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    fun observeAccountById(accountId: String): LiveData<Account>

    @Query("SELECT * FROM accounts WHERE userId = :uid")
    suspend fun getAccounts(uid: String): List<Account>

    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    suspend fun getAccountById(accountId: String): Account

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccountIgnoreOnConflict(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("DELETE FROM accounts WHERE userId = :uid")
    suspend fun deleteAccounts(uid: String): Int

    @Query("DELETE FROM accounts WHERE personalAccount = 1 AND userId = :uid")
    suspend fun deletePersonalAccounts(uid: String): Int

    @Query("DELETE FROM accounts WHERE personalAccount = 0 AND userId = :uid")
    suspend fun deleteSavedAccounts(uid: String): Int
}