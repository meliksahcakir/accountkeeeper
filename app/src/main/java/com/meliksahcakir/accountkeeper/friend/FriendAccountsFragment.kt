package com.meliksahcakir.accountkeeper.friend

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
import kotlinx.android.synthetic.main.friend_accounts_fragment.*

class FriendAccountsFragment : Fragment(), AccountAdapterListener {


    companion object {
        fun newInstance() = FriendAccountsFragment()
    }

    private val viewModel by viewModels<FriendAccountsViewModel> {
        FriendAccountsViewModelFactory((requireContext().applicationContext as AccountKeeperApplication).accountRepository)
    }

    private lateinit var friendAccountAdapter: FriendAccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.friend_accounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendAccountAdapter = FriendAccountAdapter(this)
        val itemTouchHelper =
            ItemTouchHelper(SwipeCallbacks(requireContext(), friendAccountAdapter))
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendAccountAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
        viewModel.accounts.observe(viewLifecycleOwner) {
            emptyListGroup.isVisible = it.isEmpty()
            recyclerView.isVisible = it.isNotEmpty()
            friendAccountAdapter.submitList(it)
            if (toolbarEditText.text.toString() == "") {
                emptyListTextView.text = getString(R.string.no_data_available)
            } else {
                emptyListTextView.text = getString(R.string.search_not_found)
            }
        }
        viewModel.snackBarParams.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { params ->
                showSnackBar(params)
            }
        }
        toolbarEditText.afterTextChanged {
            viewModel.updateSearchParameter(it)
        }

        setUpNavigation()
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        viewModel.accountUpdateEvent.observe(viewLifecycleOwner, EventObserver {
            val location = mainFab.findLocationOfCenterOnTheScreen()
            val action = FriendAccountsFragmentDirections
                .actionFriendAccountsFragmentToAddUpdateAccountFragment(
                    it.accountId,
                    false,
                    location
                )
            findNavController().navigate(action)
        })
        viewModel.newAccountEvent.observe(viewLifecycleOwner, EventObserver {
            val location = mainFab.findLocationOfCenterOnTheScreen()
            val action = FriendAccountsFragmentDirections
                .actionFriendAccountsFragmentToAddUpdateAccountFragment(
                    "",
                    false,
                    location
                )
            findNavController().navigate(action)
        })
        mainFab.setOnClickListener {
            viewModel.onAddButtonClicked()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onEditButtonClicked(account: Account) {
        viewModel.onUpdateButtonClicked(account)
    }

    override fun onShareAccount(account: Account) {
    }

    override fun onDeleteAccount(account: Account) {
        viewModel.deleteAccount(account)
    }

    private fun showSnackBar(parameters: SnackBarParameters) {
        val snackbar =
            Snackbar.make(requireView(), parameters.messageStringId, Snackbar.LENGTH_SHORT)
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
