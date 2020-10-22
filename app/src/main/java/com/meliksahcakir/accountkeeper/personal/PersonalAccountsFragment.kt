package com.meliksahcakir.accountkeeper.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.utils.SnackBarAction
import com.meliksahcakir.accountkeeper.utils.SnackBarParameters
import com.meliksahcakir.accountkeeper.utils.SwipeCallbacks
import com.meliksahcakir.accountkeeper.view.AccountAdapterListener
import kotlinx.android.synthetic.main.personal_accounts_fragment.*

class PersonalAccountsFragment : Fragment(), AccountAdapterListener {

    companion object {
        fun newInstance() = PersonalAccountsFragment()
    }

    private val viewModel by viewModels<PersonalAccountsViewModel> {
        PersonalAccountsViewModelFactory((requireContext().applicationContext as AccountKeeperApplication).accountRepository)
    }

    private lateinit var personalAccountAdapter: PersonalAccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.personal_accounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personalAccountAdapter = PersonalAccountAdapter(this)
        val itemTouchHelper = ItemTouchHelper(SwipeCallbacks(requireContext(), personalAccountAdapter))
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = personalAccountAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
        viewModel.accounts.observe(viewLifecycleOwner) {
            emptyListGroup.isVisible = it.isEmpty()
            recyclerView.isVisible = it.isNotEmpty()
            personalAccountAdapter.submitList(it)
        }
        viewModel.snackBarParams.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { params ->
                showSnackBar(params)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onEditButtonClicked(account: Account) {
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
                    SnackBarAction.UNDO -> viewModel.addAccount(parameters.data as Account)
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
