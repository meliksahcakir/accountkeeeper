package com.meliksahcakir.accountkeeper.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.login.LoginActivity
import com.meliksahcakir.accountkeeper.login.LoginRepository
import com.meliksahcakir.accountkeeper.preference.PreferenceRepository
import com.meliksahcakir.accountkeeper.utils.EventObserver
import com.meliksahcakir.accountkeeper.utils.SnackBarParameters
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel> {
        SettingsViewModelFactory(
            LoginRepository.getInstance(),
            (requireActivity().application as AccountKeeperApplication).accountRepository
        )
    }
    private lateinit var preferenceRepository: PreferenceRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        preferenceRepository =
            (requireActivity().application as AccountKeeperApplication).preferenceRepository
        preferenceRepository.isDarkTheme.observe(viewLifecycleOwner) {
            darkThemeSwitch.isChecked = it
        }
        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferenceRepository.isDarkThemeSelected = isChecked
        }
        viewModel.navigateToLoginScreen.observe(viewLifecycleOwner, EventObserver {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        })
        viewModel.syncBusy.observe(viewLifecycleOwner) {
            syncCardView.busy = it
        }
        viewModel.snackBarParams.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it)
        })
        profileEmailTextView.text = FirebaseAuth.getInstance().currentUser?.email
        signOutCardView.setOnClickListener {
            viewModel.onSignOutButtonClicked()
        }
        syncCardView.setOnClickListener {
            viewModel.onSyncButtonClicked()
        }
        shareProfileCardView.setOnClickListener {
            viewModel.onShareProfileButtonClicked(requireActivity())
        }
        changePasswordCardView.setOnClickListener {
            viewModel.onChangePasswordButtonClicked()
        }
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        mainFab.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showSnackBar(parameters: SnackBarParameters) {
        val snackbar =
            Snackbar.make(requireView(), parameters.messageStringId, Snackbar.LENGTH_SHORT)
        snackbar.anchorView = (requireActivity() as MainActivity).mainFab
        snackbar.show()
    }
}
