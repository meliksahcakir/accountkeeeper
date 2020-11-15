package com.meliksahcakir.accountkeeper.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.UserInfo
import com.meliksahcakir.accountkeeper.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.find_users_fragment.*

class FindUsersFragment : Fragment(), OnClickListener {

    private val viewModel: FindAccountsAndUsersViewModel by viewModels(ownerProducer = { requireActivity() }) {
        ViewModelFactory((requireActivity().application as AccountKeeperApplication).accountRepository)
    }

    private lateinit var mAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.find_users_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = UserAdapter(this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        viewModel.userList.observe(viewLifecycleOwner) {
            emptyListGroup.isVisible = it.isEmpty()
            recyclerView.isVisible = it.isNotEmpty()
            mAdapter.submitList(it)
            if (it.isEmpty()) {
                if (viewModel.searchParameter == "") {
                    emptyListTextView.text =
                        getString(R.string.search_for_users_to_see_their_accounts)
                    emptyListImageView.setImageResource(R.drawable.ic_search)
                } else {
                    emptyListTextView.text = getString(R.string.user_not_found)
                    emptyListImageView.setImageResource(R.drawable.ic_account_circle)
                }
            }
        }
        viewModel.snackBarParams.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })

        viewModel.busy.observe(viewLifecycleOwner) {
            progressBar.isVisible = it
        }

        viewModel.navigateToFindAccountsFragment.observe(viewLifecycleOwner, EventObserver {
            val direction =
                FindUsersFragmentDirections.actionFindUsersFragmentToFindAccountsFragment(null, it.uid)
            findNavController().navigate(direction)
        })

        setUpNavigation()
        toolbarEditText.setText(viewModel.searchParameter)

        toolbarEditText.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    viewModel.updateSearchParameter(toolbarEditText.text.toString().trim())
                    v.hideKeyboard()
                    true
                }
                else -> false
            }
        }
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        mainFab.setOnClickListener {
            it.hideKeyboard()
            viewModel.updateSearchParameter(toolbarEditText.text.toString())
        }
    }

    override fun onClick(user: UserInfo) {
        viewModel.onUserSelected(user)
    }

    private fun showSnackBar(parameters: SnackBarParameters) {
        val snackbar =
            Snackbar.make(requireView(), parameters.messageStringId, Snackbar.LENGTH_SHORT)
        snackbar.anchorView = (requireActivity() as MainActivity).mainFab
        if (parameters.action != SnackBarAction.NONE) {
            snackbar.setAction(parameters.actionStringId) {
                if (parameters.action == SnackBarAction.RETRY) {
                    viewModel.updateSearchParameter(viewModel.searchParameter)
                }
            }
        }
        snackbar.show()
    }
}