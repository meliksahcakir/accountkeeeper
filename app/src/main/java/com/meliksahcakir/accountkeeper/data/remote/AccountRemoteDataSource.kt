package com.meliksahcakir.accountkeeper.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.data.IAccountDataSource
import com.meliksahcakir.accountkeeper.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AccountRemoteDataSource : IAccountDataSource {

    private var db = Firebase.firestore

    private var userRef: DocumentReference? = null

    private var accountsRef: CollectionReference? = null

    private val observableAccounts = MutableLiveData<Result<List<Account>>>()

    private var uid = ""
        set(value) {
            if(value != field) {
                observableAccounts.value = null
                field = value
                userRef = db.collection("users").document(uid)
                accountsRef = userRef?.collection("accounts")
            }
        }

    override fun setUserId(uid: String) {
        this.uid = uid
    }

    override fun observeAccounts(): LiveData<Result<List<Account>>> {
        return observableAccounts
    }

    override suspend fun getAccounts(): Result<List<Account>> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = accountsRef?.get()?.await()
                if (snapshot != null) {
                    val documents = snapshot.documents
                    val list = documents.mapNotNull { it.toObject(Account::class.java) }
                    val result = Result.Success(list)
                    observableAccounts.postValue(result)
                    result
                } else {
                    Result.Error(Exception("Cannot retrieve accounts"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun observeAccount(accountId: String): LiveData<Result<Account>> {
        return observableAccounts.map { result ->
            when (result) {
                is Result.Success -> {
                    val account = result.data.firstOrNull { it.accountId == accountId }
                        ?: return@map Result.Error(
                            Exception("account not found")
                        )
                    Result.Success(account)
                }
                is Result.Error -> Result.Error(result.exception)
            }
        }
    }

    override suspend fun getAccount(accountId: String): Result<Account> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = accountsRef?.document(accountId)?.get()?.await()
                val account = snapshot?.toObject(Account::class.java)
                if (account != null) {
                    Result.Success(account)
                } else {
                    Result.Error(Exception("Cannot retrieve account"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun saveAccount(account: Account) {
        accountsRef?.document(account.accountId)?.set(account)
    }

    override suspend fun saveAccountUnlessAvailable(account: Account) {
        saveAccount(account)
    }

    override suspend fun updateAccount(account: Account) {
        saveAccount(account)
    }

    override suspend fun deleteAccount(account: Account) {
        withContext(Dispatchers.IO) {
            try {
                accountsRef?.document(account.accountId)?.delete()?.await()
            } catch (e: Exception) {

            }
        }
    }

    override suspend fun deleteAccounts(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = accountsRef?.get()?.await()
                if (snapshot != null) {
                    val count = snapshot.documents.size
                    for (document in snapshot.documents) {
                        document.reference.delete()
                    }
                    count
                } else {
                    0
                }
            } catch (e: Exception) {
                0
            }
        }
    }

    override suspend fun deletePersonalAccounts(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = accountsRef?.whereEqualTo("personalAccount", true)?.get()?.await()
                if (snapshot != null) {
                    val count = snapshot.documents.size
                    for (document in snapshot.documents) {
                        document.reference.delete()
                    }
                    count
                } else {
                    0
                }
            } catch (e: Exception) {
                0
            }
        }
    }

    override suspend fun deleteSavedAccounts(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = accountsRef?.whereEqualTo("personalAccount", false)?.get()?.await()
                if (snapshot != null) {
                    val count = snapshot.documents.size
                    for (document in snapshot.documents) {
                        document.reference.delete()
                    }
                    count
                } else {
                    0
                }
            } catch (e: Exception) {
                0
            }
        }
    }
}