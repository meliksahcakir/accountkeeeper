package com.meliksahcakir.accountkeeper.friend

import android.view.ViewGroup
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.view.AccountAdapterListener
import com.meliksahcakir.accountkeeper.view.BaseAccountAdapter
import com.meliksahcakir.accountkeeper.view.BaseAccountViewHolder

class FriendAccountAdapter(
    private val listener: AccountAdapterListener
) : BaseAccountAdapter(listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendAccountsViewHolder {
        return FriendAccountsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseAccountViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

class FriendAccountsViewHolder(
    private val parent: ViewGroup
) : BaseAccountViewHolder(parent) {

    init {
        changeBackground(R.drawable.saved_account_background)
    }
}