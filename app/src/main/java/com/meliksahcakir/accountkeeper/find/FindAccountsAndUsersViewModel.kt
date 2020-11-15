package com.meliksahcakir.accountkeeper.find

import androidx.core.text.trimmedLength
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.data.UserInfo
import com.meliksahcakir.accountkeeper.utils.Event
import com.meliksahcakir.accountkeeper.utils.Result
import com.meliksahcakir.accountkeeper.utils.SnackBarAction
import com.meliksahcakir.accountkeeper.utils.SnackBarParameters
import kotlinx.coroutines.launch
import java.util.*

class FindAccountsAndUsersViewModel(private val repository: AccountRepository) : ViewModel() {

    var searchParameter = ""
        private set

    private val _userList = MutableLiveData<List<UserInfo>>(emptyList())
    val userList: LiveData<List<UserInfo>> = _userList

    private val _accounts = MutableLiveData<List<Account>>(emptyList())
    val accounts: LiveData<List<Account>> = _accounts

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

    private val _snackBarParams = MutableLiveData<Event<SnackBarParameters>>()
    val snackBarParams: LiveData<Event<SnackBarParameters>> = _snackBarParams

    private val _navigateToFindAccountsFragment = MutableLiveData<Event<UserInfo>>()
    val navigateToFindAccountsFragment: LiveData<Event<UserInfo>> = _navigateToFindAccountsFragment

    private val _userInfoAvailable = MutableLiveData<Event<UserInfo>>()
    val userInfoAvailable: LiveData<Event<UserInfo>> = _userInfoAvailable

    private var selectedUser: UserInfo? = null

    private val _saveAccountEvent = MutableLiveData<Event<Account>>()
    val saveAccountEvent: LiveData<Event<Account>> = _saveAccountEvent

    fun updateSearchParameter(username: String) {
        searchParameter = username
        if (username.trimmedLength() < 3) {
            _userList.value = emptyList()
            _snackBarParams.value =
                Event(SnackBarParameters(R.string.try_to_search_for_longer_name))
        } else {
            viewModelScope.launch {
                val result = repository.getRemoteUserList(username.toLowerCase(Locale.getDefault()))
                if (result is Result.Success) {
                    _userList.value = result.data
                } else {
                    result as Result.Error
                    result.exception.printStackTrace()
                    _snackBarParams.value = Event(
                        SnackBarParameters(
                            R.string.user_not_found,
                            null,
                            SnackBarAction.RETRY,
                            R.string.retry
                        )
                    )
                }
            }
        }
    }

    fun onUserSelected(userInfo: UserInfo) {
        selectedUser = userInfo
        _navigateToFindAccountsFragment.value = Event(userInfo)
    }

    fun getUserInfoAndAccounts(userId: String?, accountId: String?) {
        viewModelScope.launch {
            if (selectedUser != null && selectedUser?.uid == userId) {
                _userInfoAvailable.value = Event(selectedUser!!)
                getUserAccounts(userId!!, accountId)
            } else {
                if (userId == null) {
                    _snackBarParams.value = Event(SnackBarParameters(R.string.user_not_found))
                } else {
                    _busy.value = true
                    val result = repository.getRemoteUserInfo(userId)
                    if (result is Result.Success) {
                        selectedUser = result.data
                        _userInfoAvailable.value = Event(selectedUser!!)
                        getUserAccounts(userId, accountId)
                    } else {
                        _snackBarParams.value = Event(SnackBarParameters(R.string.user_not_found))
                    }
                    _busy.value = false
                }
            }
        }
    }

    private suspend fun getUserAccounts(userId: String, accountId: String?) {
        val accountsResult = repository.getRemoteAccountList(userId, accountId)
        if (accountsResult is Result.Success) {
            _accounts.value = accountsResult.data
        } else {
            _snackBarParams.value = Event(SnackBarParameters(R.string.accounts_not_found))
        }
    }

    fun onSaveButtonClicked(account: Account) {
        if (selectedUser == null) return
        val acc = with(account) {
            Account(
                accountName = "${selectedUser!!.username} - $accountName",
                accountNumber = accountNumber,
                accountDescription = accountDescription,
                personalAccount = false,
                global = false,
                accountType = accountType,
                userId = repository.getUserId(),
                accountId = accountId
            )
        }
        _saveAccountEvent.value = Event(acc)
    }
}
