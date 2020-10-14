package com.meliksahcakir.accountkeeper.find

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.meliksahcakir.accountkeeper.R

class FindAccountsFragment : Fragment() {

    companion object {
        fun newInstance() = FindAccountsFragment()
    }

    private lateinit var viewModel: FindAccountsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.find_accounts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FindAccountsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
