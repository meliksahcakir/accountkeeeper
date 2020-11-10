package com.meliksahcakir.accountkeeper.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.utils.*
import com.meliksahcakir.accountkeeper.view.AccountAdapterListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.personal_accounts_fragment.*

class PersonalAccountsFragment : Fragment(), AccountAdapterListener {

    private val viewModel by viewModels<PersonalAccountsViewModel> {
        ViewModelFactory((requireContext().applicationContext as AccountKeeperApplication).accountRepository)
    }

    private lateinit var mAdapter: PersonalAccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.personal_accounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = PersonalAccountAdapter(this)
        val itemTouchHelper = ItemTouchHelper(SwipeCallbacks(requireContext(), mAdapter))
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
        viewModel.accounts.observe(viewLifecycleOwner) {
            emptyListGroup.isVisible = it.isEmpty()
            recyclerView.isVisible = it.isNotEmpty()
            mAdapter.submitList(it)
            if (toolbarEditText.text.toString() == "") {
                emptyListTextView.text = getString(R.string.no_data_available)
            } else {
                emptyListTextView.text = getString(R.string.search_not_found)
            }
        }
        viewModel.snackBarParams.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        toolbarEditText.afterTextChanged {
            viewModel.updateSearchParameter(it)
        }

        setUpNavigation()
        arguments?.let {
            val args = PersonalAccountsFragmentArgs.fromBundle(it)
            val message = args.snackBarText
            message?.let { str ->
                val snackbar = Snackbar.make(requireView(), str, Snackbar.LENGTH_SHORT)
                snackbar.anchorView = (requireActivity() as MainActivity).mainFab
                snackbar.show()
            }
        }
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        viewModel.accountUpdateEvent.observe(viewLifecycleOwner, EventObserver {
            val location = mainFab.findLocationOfCenterOnTheScreen()
            val action = PersonalAccountsFragmentDirections
                .actionPersonalAccountsFragmentToAddUpdateAccountFragment(
                    it.accountId,
                    true,
                    location
                )
            findNavController().navigate(action)
        })
        viewModel.newAccountEvent.observe(viewLifecycleOwner, EventObserver {
            val location = mainFab.findLocationOfCenterOnTheScreen()
            val action = PersonalAccountsFragmentDirections
                .actionPersonalAccountsFragmentToAddUpdateAccountFragment(
                    "",
                    true,
                    location
                )
            findNavController().navigate(action)
        })
        mainFab.setOnClickListener {
            viewModel.onAddButtonClicked()
        }
    }

    override fun onEditButtonClicked(account: Account) {
        viewModel.onUpdateButtonClicked(account)
    }

    override fun onShareAccount(account: Account) {
        viewModel.onShareButtonClicked(requireActivity(), account)
    }

    override fun onDeleteAccount(account: Account) {
        viewModel.deleteAccount(account)
    }

    private fun showSnackBar(parameters: SnackBarParameters) {
        val snackbar =
            Snackbar.make(requireView(), parameters.messageStringId, Snackbar.LENGTH_SHORT)
        snackbar.anchorView = (requireActivity() as MainActivity).mainFab
        if (parameters.action != SnackBarAction.NONE) {
            snackbar.setAction(parameters.actionStringId) {
                when (parameters.action) {
                    SnackBarAction.UNDO -> viewModel.undoAccount(parameters.data as Account)
                    SnackBarAction.NONE -> TODO()
                    SnackBarAction.OPEN -> TODO()
                    SnackBarAction.SAVE -> TODO()
                    SnackBarAction.SHARE -> TODO()
                    SnackBarAction.RETRY -> TODO()
                    SnackBarAction.CANCEL -> TODO()
                    SnackBarAction.SKIP -> TODO()
                    SnackBarAction.OK -> TODO()
                }
            }
        }
        snackbar.show()
    }

}
