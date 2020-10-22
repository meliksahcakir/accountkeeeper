package com.meliksahcakir.accountkeeper.friend

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

import com.meliksahcakir.accountkeeper.R

class FriendAccountsFragment : Fragment() {

    companion object {
        fun newInstance() = FriendAccountsFragment()
    }

    private val viewModel: FriendAccountsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.friend_accounts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
