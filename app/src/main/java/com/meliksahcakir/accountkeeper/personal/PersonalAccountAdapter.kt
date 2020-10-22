package com.meliksahcakir.accountkeeper.personal

import android.view.ViewGroup
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.view.AccountAdapterListener
import com.meliksahcakir.accountkeeper.view.BaseAccountAdapter
import com.meliksahcakir.accountkeeper.view.BaseAccountViewHolder

class PersonalAccountAdapter(
    private val listener: AccountAdapterListener
) : BaseAccountAdapter(listener) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalAccountsViewHolder {
        return PersonalAccountsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseAccountViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

class PersonalAccountsViewHolder(
    private val parent: ViewGroup
) : BaseAccountViewHolder(parent) {

    init {
        changeBackground(R.drawable.personal_account_background)
    }
}