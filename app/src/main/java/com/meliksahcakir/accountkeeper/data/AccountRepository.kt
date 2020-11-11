package com.meliksahcakir.accountkeeper.data

import androidx.lifecycle.LiveData
import com.meliksahcakir.accountkeeper.utils.Result
import kotlinx.coroutines.*

class AccountRepository(
    private val localDataSource: IAccountDataSource,
    private val remoteDataSource: IAccountDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private var uid = ""

    fun setUserId(uid: String) {
        this.uid = uid
        localDataSource.setUserId(uid)
        remoteDataSource.setUserId(uid)
    }

    fun getUserId() = uid

    suspend fun getAccounts(forceUpdate: Boolean = false): Result<List<Account>> {
        if (forceUpdate) {
            try {
                updateAccountsFromRemoteDataSource()
            } catch (e: Exception) {
                return Result.Error(e)
            }
        }
        return localDataSource.getAccounts()
    }

    suspend fun refreshAccounts() {
        updateAccountsFromRemoteDataSource()
    }

    fun observeAccounts(): LiveData<Result<List<Account>>> {
        return localDataSource.observeAccounts()
    }

    private suspend fun updateAccountsFromRemoteDataSource() {
        val remoteAccounts = remoteDataSource.getAccounts()
        if (remoteAccounts is Result.Success) {
            remoteAccounts.data.forEach {
                localDataSource.saveAccountUnlessAvailable(it)
            }
        } else if (remoteAccounts is Result.Error) {
            throw remoteAccounts.exception
        }
    }

    private suspend fun updateAccountFromRemoteDataSource(accountId: String) {
        val remoteAccount = remoteDataSource.getAccount(accountId)
        if (remoteAccount is Result.Success) {
            localDataSource.saveAccountUnlessAvailable(remoteAccount.data)
        }
    }

    suspend fun getAccount(accountId: String, forceUpdate: Boolean = false): Result<Account> {
        if (forceUpdate) {
            updateAccountFromRemoteDataSource(accountId)
        }
        return localDataSource.getAccount(accountId)
    }

    suspend fun saveAccount(account: Account) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remoteDataSource.saveAccount(account) }
                launch { localDataSource.saveAccount(account) }
            }
        }
    }

    suspend fun updateAccount(account: Account) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remoteDataSource.updateAccount(account) }
                launch { localDataSource.updateAccount(account) }
            }
        }
    }

    suspend fun deleteAccount(account: Account) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remoteDataSource.deleteAccount(account) }
                launch { localDataSource.deleteAccount(account) }
            }
        }
    }

    suspend fun deleteAccounts() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remoteDataSource.deleteAccounts() }
                launch { localDataSource.deleteAccounts() }
            }
        }
    }

    suspend fun deletePersonalAccounts() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remoteDataSource.deletePersonalAccounts() }
                launch { localDataSource.deletePersonalAccounts() }
            }
        }
    }

    suspend fun deleteSavedAccounts() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { remoteDataSource.deleteSavedAccounts() }
                launch { localDataSource.deleteSavedAccounts() }
            }
        }
    }

    suspend fun updateRemoteUserInfo(userInfo: UserInfo): Result<Unit> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.updateRemoteUserInfo(userInfo)
        }
    }

    suspend fun getRemoteUserInfo(): Result<UserInfo> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.getRemoteUserInfo()
        }
    }

    suspend fun getRemoteUserList(userName: String): Result<List<UserInfo>> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.getRemoteUserList(userName)
        }
    }
}