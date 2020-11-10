package com.meliksahcakir.accountkeeper.addupdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.meliksahcakir.accountkeeper.AccountKeeperApplication
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_update_account_fragment.*

class AddUpdateAccountFragment : Fragment(), ExitWithAnimation {

    private val viewModel by viewModels<AddUpdateAccountViewModel> {
        ViewModelFactory((requireContext().applicationContext as AccountKeeperApplication).accountRepository)
    }

    private var accountId = ""
    private var personal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = AddUpdateAccountFragmentArgs.fromBundle(it)
            args.exitLocation?.let { location ->
                posX = location[0]
                posY = location[1]
            }
            accountId = args.accountId ?: ""
            personal = args.personal
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_update_account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(
            requireContext().color(R.color.colorPrimary),
            requireContext().color(R.color.colorBackground),
            posX,
            posY
        )
        accountNameEditText.afterTextChanged {
            accountNameErrorImageView.isVisible = false
        }
        accountNumberEditText.afterTextChanged {
            accountNumberErrorImageView.isVisible = false
        }
        personalChipGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.personalYesChip) {
                privacyYesChip.isEnabled = true
                privacyNoChip.isEnabled = true
            } else {
                privacyYesChip.isEnabled = false
                privacyNoChip.isEnabled = false
                privacyChipGroup.check(R.id.privacyYesChip)
            }
        }
        setUpNavigation()
        viewModel.busy.observe(viewLifecycleOwner) {
            progressBar.isInvisible = !it
            layout.isEnabled = !it
        }
        viewModel.accountInitialized.observe(viewLifecycleOwner, EventObserver {
            accountNameEditText.setText(it.accountName)
            accountDescriptionEditText.setText(it.accountDescription)
            accountNumberEditText.setText(it.accountNumber)
            (accountTypeChipGroup.getChildAt(it.accountType) as Chip).isChecked = true
            personalChipGroup.check(if (it.personalAccount) R.id.personalYesChip else R.id.personalNoChip)
            privacyChipGroup.check(if (it.global) R.id.privacyNoChip else R.id.privacyYesChip)
        })
        viewModel.accountSaved.observe(viewLifecycleOwner, EventObserver { personal ->
            val message = getString(R.string.account_saved_successfully)
            val navDirection = if (personal) {
                AddUpdateAccountFragmentDirections
                    .actionAddUpdateAccountFragmentToPersonalAccountsFragment(
                        message
                    )
            } else {
                AddUpdateAccountFragmentDirections
                    .actionAddUpdateAccountFragmentToFriendAccountsFragment(
                        message
                    )
            }
            if (posX == null || posY == null) {
                findNavController().navigate(navDirection)
            } else {
                view.exitCircularReveal(
                    posX!!,
                    posY!!,
                    requireContext().color(R.color.colorBackground),
                    requireContext().color(R.color.colorPrimary)
                ) {
                    findNavController().navigate(navDirection)
                }
            }
        })
        viewModel.start(accountId, personal)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (posX == null || posY == null) {
                findNavController().navigateUp()
            } else {
                view.exitCircularReveal(
                    posX!!,
                    posY!!,
                    requireContext().color(R.color.colorBackground),
                    requireContext().color(R.color.colorPrimary)
                ) {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        mainFab.setOnClickListener {
            val name = accountNameEditText.text.toString()
            val desc = accountDescriptionEditText.text.toString()
            val number = accountNumberEditText.text.toString()
            val typeChipId = accountTypeChipGroup.checkedChipId
            var type = 0
            type = when (typeChipId) {
                R.id.bankChip -> Account.BANK_ACCOUNT
                R.id.cryptoChip -> Account.CRYPTO
                R.id.emailChip -> Account.EMAIL
                R.id.phoneChip -> Account.PHONE
                R.id.locationChip -> Account.LOCATION
                else -> Account.OTHER
            }
            val personal = personalChipGroup.checkedChipId == R.id.personalYesChip
            val global = personal && privacyChipGroup.checkedChipId == R.id.privacyNoChip
            if (isRequiredFieldsAreAvailable()) {
                viewModel.saveAccount(name, number, desc, type, personal, global)
            }
        }
    }

    private fun isRequiredFieldsAreAvailable(): Boolean {
        val name = accountNameEditText.text.toString()
        val number = accountNumberEditText.text.toString()
        var status = true
        if (name == "") {
            accountNameErrorImageView.isVisible = true
            status = false
        }
        if (number == "") {
            accountNumberErrorImageView.isVisible = true
            status = false
        }
        return status
    }

    override var posX: Int? = null
    override var posY: Int? = null
    override fun isToBeExitedWithAnimation(): Boolean = true

}