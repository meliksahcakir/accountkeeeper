package com.meliksahcakir.accountkeeper.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.utils.EventObserver
import com.meliksahcakir.accountkeeper.utils.ViewModelFactory
import com.meliksahcakir.accountkeeper.utils.drawable
import com.meliksahcakir.accountkeeper.utils.findLocationOfCenterOnTheScreen
import com.meliksahcakir.accountkeeper.view.AccountAdapterListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.find_accounts_fragment.*
import timber.log.Timber

class FindAccountsFragment : Fragment(), AccountAdapterListener {

    private val viewModel: FindAccountsAndUsersViewModel by viewModels() {
        ViewModelFactory((requireActivity().application as AccountKeeperApplication).accountRepository)
    }

    private lateinit var mAdapter: FindAccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.find_accounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        mAdapter = FindAccountAdapter(this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        arguments?.let {
            val args = FindAccountsFragmentArgs.fromBundle(it)
            val userId = args.userId
            val accountId = args.accountId
            Timber.d("args: uid: $userId  accountId: $accountId")
            viewModel.getUserInfoAndAccounts(userId, accountId)
        }
        viewModel.accounts.observe(viewLifecycleOwner) {
            emptyListGroup.isVisible = it.isEmpty()
            recyclerView.isVisible = it.isNotEmpty()
            mAdapter.submitList(it)
        }
        viewModel.busy.observe(viewLifecycleOwner) {
            progressBar.isVisible = it
        }
        viewModel.userInfoAvailable.observe(viewLifecycleOwner, EventObserver {
            userCardView.title = it.username
            userCardView.subTitle = it.email
            userCardView.drawableStart = context?.drawable(R.drawable.ic_account_keeper)
        })
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        mainFab.setOnClickListener {
            findNavController().navigateUp()
        }
        viewModel.saveAccountEvent.observe(viewLifecycleOwner, EventObserver {
            val location = mainFab.findLocationOfCenterOnTheScreen()
            val action = FindAccountsFragmentDirections
                .actionFindAccountsFragmentToAddUpdateAccountFragment(
                    it.accountId,
                    false,
                    location,
                     it
                )
            findNavController().navigate(action)
        })
    }

    override fun onEditButtonClicked(account: Account) {

    }

    override fun onShareAccount(account: Account) {

    }

    override fun onDeleteAccount(account: Account) {

    }

    override fun onSaveAccount(account: Account) {
        viewModel.onSaveButtonClicked(account)
    }
}
