package com.meliksahcakir.accountkeeper.addupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.data.AccountRepository
import com.meliksahcakir.accountkeeper.utils.Event
import com.meliksahcakir.accountkeeper.utils.Result
import kotlinx.coroutines.launch
import java.util.*

class AddUpdateAccountViewModel(private val repository: AccountRepository) : ViewModel() {

    private var accountId = ""
    private var newAccount = false

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

    private val _accountSaved = MutableLiveData<Event<Boolean>>()
    val accountSaved: LiveData<Event<Boolean>> = _accountSaved

    private val _accountInitialized = MutableLiveData<Event<Account>>()
    val accountInitialized: LiveData<Event<Account>> = _accountInitialized

    private var account: Account? = null

    fun start(account: Account) {
        this.accountId = account.accountId
        newAccount = true
        _busy.value = true
        this.account = account
        viewModelScope.launch {
            repository.getAccount(accountId).let {
                if (it is Result.Success) {
                    account.accountId = UUID.randomUUID().toString()
                }
                _busy.value = false
                _accountInitialized.value = Event(account)
            }
        }
    }

    fun start(accountId: String, personal: Boolean) {
        if (_busy.value == true) {
            return
        }
        this.accountId = accountId
        if (accountId == "") {
            newAccount = true
            account = Account(personalAccount = personal, userId = repository.getUserId())
            _accountInitialized.value = Event(account!!)
            return
        }
        if (account != null) {
            return
        }
        newAccount = false
        _busy.value = true
        viewModelScope.launch {
            repository.getAccount(accountId).let {
                account = if (it is Result.Success) {
                    it.data
                } else {
                    Account(personalAccount = personal, userId = repository.getUserId())
                }
                _busy.value = false
                _accountInitialized.value = Event(account!!)
            }
        }
    }

    fun saveAccount(
        name: String,
        number: String,
        desc: String,
        type: Int,
        personal: Boolean,
        global: Boolean
    ) {
        account?.let {
            it.update(name, number, desc, type, personal, global)
            viewModelScope.launch {
                _busy.value = true
                if (newAccount) {
                    repository.saveAccount(it)
                } else {
                    repository.updateAccount(it)
                }
                _busy.value = false
                _accountSaved.value = Event(it.personalAccount)
            }
        }
    }
}
