package com.meliksahcakir.accountkeeper.data

import androidx.lifecycle.LiveData
import com.meliksahcakir.accountkeeper.utils.Result

interface IAccountDataSource {

    fun setUserId(uid: String)

    fun observeAccounts(): LiveData<Result<List<Account>>>

    suspend fun getAccounts(): Result<List<Account>>

    fun observeAccount(accountId: String): LiveData<Result<Account>>

    suspend fun getAccount(accountId: String): Result<Account>

    suspend fun saveAccount(account: Account)

    suspend fun saveAccountUnlessAvailable(account: Account)

    suspend fun updateAccount(account: Account)

    suspend fun deleteAccount(account: Account)

    suspend fun deleteAccounts(): Int

    suspend fun deletePersonalAccounts(): Int

    suspend fun deleteSavedAccounts(): Int

    suspend fun updateRemoteUserInfo(userInfo: UserInfo): Result<Unit>

    suspend fun getRemoteUserInfo(): Result<UserInfo>

    suspend fun getRemoteUserList(userName: String): Result<List<UserInfo>>
}