package com.meliksahcakir.accountkeeper.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import kotlinx.android.synthetic.main.activity_main.*

class FindAccountsFragment : Fragment() {

    companion object {
        fun newInstance() = FindAccountsFragment()
    }

    private val viewModel: FindAccountsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.find_accounts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        arguments?.let {
            val args = FindAccountsFragmentArgs.fromBundle(it)
            val userId = args.userId
            val accountId = args.accountId
            Toast.makeText(
                requireContext(),
                "uid: $userId  accountId: $accountId",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        mainFab.setOnClickListener {

        }
    }

}
