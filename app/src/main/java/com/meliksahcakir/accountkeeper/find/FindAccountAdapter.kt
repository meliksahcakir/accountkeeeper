package com.meliksahcakir.accountkeeper.find

import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.Account
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
        editImageView.isInvisible = true
        shareImageView.setImageResource(R.drawable.ic_download)
    }

    override fun bind(account: Account, listener: AccountAdapterListener) {
        super.bind(account, listener)
        shareImageView.setOnClickListener {
            listener.onSaveAccount(account)
        }
    }
}