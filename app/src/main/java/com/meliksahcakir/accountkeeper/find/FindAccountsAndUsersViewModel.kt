package com.meliksahcakir.accountkeeper.find

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.data.UserInfo
import com.meliksahcakir.accountkeeper.utils.Event
import com.meliksahcakir.accountkeeper.utils.Result
import com.meliksahcakir.accountkeeper.utils.SnackBarAction
import com.meliksahcakir.accountkeeper.utils.SnackBarParameters
import kotlinx.coroutines.launch

class FindAccountsAndUsersViewModel(private val repository: AccountRepository) : ViewModel() {

    var searchParameter = ""
        private set

    private val _userList = MutableLiveData<List<UserInfo>>(emptyList())
    val userList: LiveData<List<UserInfo>> = _userList

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

    private val _snackBarParams = MutableLiveData<Event<SnackBarParameters>>()
    val snackBarParams: LiveData<Event<SnackBarParameters>> = _snackBarParams

    private val _navigateToFindAccountsFragment = MutableLiveData<Event<UserInfo>>()
    val navigateToFindAccountsFragment: LiveData<Event<UserInfo>> = _navigateToFindAccountsFragment

    private var selectedUser: UserInfo? = null

    fun updateSearchParameter(username: String) {
        searchParameter = username
        if (username == "") {
            _userList.value = emptyList()
        } else {
            viewModelScope.launch {
                val result = repository.getRemoteUserList(username)
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
}
