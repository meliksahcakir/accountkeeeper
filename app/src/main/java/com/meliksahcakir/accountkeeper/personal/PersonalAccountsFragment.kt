package com.meliksahcakir.accountkeeper.personal

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.meliksahcakir.accountkeeper.R

class PersonalAccountsFragment : Fragment() {

    companion object {
        fun newInstance() = PersonalAccountsFragment()
    }

    private lateinit var viewModel: PersonalAccountsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.personal_accounts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PersonalAccountsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
