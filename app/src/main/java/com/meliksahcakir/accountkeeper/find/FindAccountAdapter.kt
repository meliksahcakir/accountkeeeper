package com.meliksahcakir.accountkeeper.find

import android.view.ViewGroup
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.view.AccountAdapterListener
import com.meliksahcakir.accountkeeper.view.BaseAccountAdapter
import com.meliksahcakir.accountkeeper.view.BaseAccountViewHolder

class FindAccountAdapter(
    private val listener: AccountAdapterListener
) : BaseAccountAdapter(listener) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindAccountsViewHolder {
        return FindAccountsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseAccountViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

class FindAccountsViewHolder(
    private val parent: ViewGroup
) : BaseAccountViewHolder(parent) {

    init {
        changeBackground(R.drawable.personal_account_background)
    }
}