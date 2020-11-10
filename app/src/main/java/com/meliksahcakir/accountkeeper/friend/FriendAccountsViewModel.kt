package com.meliksahcakir.accountkeeper.friend

import android.content.Context
import androidx.lifecycle.*
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.utils.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class FriendAccountsViewModel(private val repository: AccountRepository) : ViewModel() {

    private val _searchText = MutableLiveData<String>("")

    private val _accounts: LiveData<List<Account>> = _searchText.switchMap { search ->
        repository.observeAccounts().switchMap { filterAccounts(search, it) }
    }

    val accounts: LiveData<List<Account>> = _accounts

    private val _snackBarParams = MutableLiveData<Event<SnackBarParameters>>()
    val snackBarParams: LiveData<Event<SnackBarParameters>> = _snackBarParams

    private val _accountUpdateEvent = MutableLiveData<Event<Account>>()
    val accountUpdateEvent: LiveData<Event<Account>> = _accountUpdateEvent

    private val _newAccountEvent = MutableLiveData<Event<Unit>>()
    val newAccountEvent: LiveData<Event<Unit>> = _newAccountEvent

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        _searchText.value = ""
    }

    private fun filterAccounts(
        search: String,
        accountResult: Result<List<Account>>
    ): LiveData<List<Account>> {
        val result = MutableLiveData<List<Account>>()
        if (accountResult is Result.Success) {
            viewModelScope.launch {
                result.value = filterAccounts(search, accountResult.data)
            }
        } else {
            result.value = emptyList()
            _snackBarParams.value = Event(SnackBarParameters(R.string.error_loading_accounts))
        }

        return result
    }

    private fun filterAccounts(search: String, accounts: List<Account>): List<Account> {
        val list = ArrayList<Account>()
        for (account in accounts) {
            if (!account.personalAccount) {
                val accountName = account.accountName.toLowerCase(Locale.getDefault())
                val searchLc = search.toLowerCase(Locale.getDefault())
                if (searchLc == "" || accountName.contains(searchLc)) {
                    list.add(account)
                }
            }
        }
        return list
    }

    fun onAddButtonClicked() {
        _newAccountEvent.value = Event(Unit)
    }

    fun onUpdateButtonClicked(account: Account) {
        _accountUpdateEvent.value = Event(account)
    }

    fun undoAccount(account: Account) {
        viewModelScope.launch {
            repository.saveAccount(account)
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            repository.deleteAccount(account)
            _snackBarParams.value =
                Event(
                    SnackBarParameters(
                        R.string.account_deleted,
                        account,
                        SnackBarAction.UNDO,
                        R.string.undo
                    )
                )
        }
    }

    fun updateSearchParameter(search: String) {
        if (_searchText.value != search) {
            _searchText.value = search
        }
    }

    fun onShareButtonClicked(activityContext: Context, account: Account) {
        viewModelScope.launch {
            try {
                val link = createDynamicLinkForTheAccount(
                    repository.getUserId(),
                    account.accountId
                ).await()
                val uriString = link.shortLink.toString()
                val message =
                    activityContext.getString(R.string.click_the_link_for_details) + "\n" + uriString
                activityContext.share(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FriendAccountsViewModelFactory(
    private val repository: AccountRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return (FriendAccountsViewModel(repository) as T)
    }
}
